package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class JailCardHandlingRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            if (player.hasEscapeCard()) {
                player.setEscapeCard(false);
                player.resetSuspension();

                return new GameMessage(MessageType.DRAW_EVENT_RISIKO_CARD, Map.of(
                        "playerId", playerId,
                        "message", "Du wurdest aus dem Gefängnis entlassen!"
                ));
            } else {
                player.suspendForRounds(3); // z.B. 3 Runden
                return new GameMessage(MessageType.GO_TO_JAIL, Map.of(
                        "playerId", playerId,
                        "tilePos", 10,
                        "tileName", "Gefängnis"
                ));
            }

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Gefängnis-Handling: " + e.getMessage());
        }
    }
}