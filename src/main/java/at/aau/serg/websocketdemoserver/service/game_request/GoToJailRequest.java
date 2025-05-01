package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class GoToJailRequest implements GameHandlerInterface {

    private static final int JAIL_TILE_INDEX = 10;
    private static final String JAIL_TILE_NAME = "Gefängnis";
    private static final int SUSPENSION_ROUNDS = 3;

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            player.suspendForRounds(SUSPENSION_ROUNDS);
            player.moveToTile(JAIL_TILE_INDEX);

            Tile jailTile = gameState.getBoard().getTile(JAIL_TILE_INDEX);

            return new GameMessage(MessageType.GO_TO_JAIL, Map.of(
                    "playerId", playerId,
                    "tilePos", jailTile.getIndex(),
                    "tileName", jailTile.getLabel()
            ));
        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Gefängnis-Request: " + e.getMessage());
        }
    }
}