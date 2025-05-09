package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;

public class BuildHotelRequest implements GameRequest {

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            int playerId = obj.getInt("playerId");
            int tilePos = obj.getInt("tilePos");

            Player player = gameState.getPlayer(playerId);
            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder ausgeschieden.");
            }

            Tile tile = gameState.getBoard().getTile(tilePos);
            if (!(tile instanceof StreetTile street)) {
                return MessageFactory.error(lobbyId, "Kein baubares Feld.");
            }

            if (!street.isOwner(player)) {
                return MessageFactory.error(lobbyId, "Du besitzt dieses Grundstück nicht.");
            }

            boolean success = street.buildHotel(player);
            if (!success) {
                return MessageFactory.error(lobbyId, "Hotelbau nicht möglich (nicht genug Geld oder weniger als 4 Häuser).");
            }

            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Hotelbau: " + e.getMessage());
        }
    }
}
