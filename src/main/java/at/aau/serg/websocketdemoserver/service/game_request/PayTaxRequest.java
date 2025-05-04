package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PayTaxPayload;
import at.aau.serg.websocketdemoserver.dto.PlayerLostPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class PayTaxRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        PayTaxPayload payload = message.parsePayload(PayTaxPayload.class);
        Player player = gameState.getPlayer(payload.getPlayerId());

        if (player == null) return GameMessage.error("Spieler nicht gefunden");

        int amount = payload.getAmount();
        int oldCash = player.getCash();

        GameMessage bankrupt = player.deductCash(amount);
        int newCash = player.getCash();

        PayTaxPayload response = new PayTaxPayload(
                player.getId(),
                payload.getTileName(),
                amount,
                oldCash,
                newCash
        );

        if (bankrupt != null) {
            gameState.removePlayer(player);
            ((PlayerLostPayload) bankrupt.getPayload()).setGameOver(gameState.getPlayers().size() <= 1);
            return bankrupt;
        }

        return new GameMessage(MessageType.TAX_PAID, response);
    }
}
