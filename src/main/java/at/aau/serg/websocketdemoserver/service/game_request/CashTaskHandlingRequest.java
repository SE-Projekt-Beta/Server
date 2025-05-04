package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.CashTaskPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class CashTaskHandlingRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        CashTaskPayload payload = message.parsePayload(CashTaskPayload.class);
        int playerId = payload.getPlayerId();
        int amount = payload.getAmount();

        Player player = gameState.getPlayer(playerId);
        if (player == null) {
            return GameMessage.error("Spieler nicht gefunden.");
        }

        if (amount < 0) {
            // Bankzahlung → Bankrott prüfen
            GameMessage bankrupt = player.deductCash(-amount); // -(-100) = +100 abziehen
            if (bankrupt != null) {
                return bankrupt;
            }
        } else {
            player.setCash(player.getCash() + amount);
        }

        CashTaskPayload response = new CashTaskPayload(playerId, amount, player.getCash());
        return new GameMessage(MessageType.CASH_TASK, response);
    }
}
