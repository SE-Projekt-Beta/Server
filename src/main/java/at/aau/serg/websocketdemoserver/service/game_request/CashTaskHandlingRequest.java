package at.aau.serg.websocketdemoserver.service.game_request;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class CashTaskHandlingRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");
            int amount = obj.getInt("amount");
            String title = obj.optString("title", "Bank-/Risiko-Karte");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            int oldCash = player.getCash();
            int newCash = oldCash + amount;
            player.setCash(newCash);

            if (newCash < 0) {
                return new GameMessage(MessageType.ERROR, "Spieler hat kein Geld mehr!");
            }

            return new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, Map.of(
                    "playerId", playerId,
                    "title", title,
                    "amount", amount,
                    "newCash", newCash
            ));

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Task-Handling: " + e.getMessage());
        }
    }
}
