package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.cards.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class DrawEventCardRequest implements GameHandlerInterface {

    private final BankCardDeck bankDeck = BankCardDeck.get();
    private final RiskCardDeck riskDeck = RiskCardDeck.get();

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        DrawEventCardPayload payload = message.parsePayload(DrawEventCardPayload.class);
        int playerId = payload.getPlayerId();

        Player player = gameState.getPlayer(playerId);
        if (player == null) {
            return GameMessage.error("Spieler nicht gefunden.");
        }

        Tile currentTile = player.getCurrentTile();

        if (currentTile instanceof BankTile) {
            BankCard card = bankDeck.drawCard();
            return card.execute(player);
        }

        if (currentTile instanceof RiskTile) {
            RiskCard card = riskDeck.drawCard();
            return card.execute(player);
        }

        return GameMessage.error("Keine Ereigniskarte auf diesem Feld verfügbar.");
    }
}
