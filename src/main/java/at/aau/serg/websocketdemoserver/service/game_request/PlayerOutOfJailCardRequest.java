package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class PlayerOutOfJailCardRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            player.setEscapeCard(true);

            return new GameMessage(MessageType.PLAYER_OUT_OF_JAIL_CARD, Map.of(
                    "playerId", player.getId(),
                    "playerName", player.getNickname()
            ));

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Ausgeben der Gefängniskarte: " + e.getMessage());
        }
    }
}
