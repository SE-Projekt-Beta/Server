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
import java.util.Map;

public class PayRentRequest implements GameRequest {

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) payload;
            JSONObject obj = new JSONObject(map);

            int playerId = obj.getInt("playerId");
            int tilePos = obj.getInt("tilePos");

            Player player = gameState.getPlayer(playerId);
            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder bereits ausgeschieden.");
            }

            Tile tile = gameState.getBoard().getTile(tilePos);
            if (!(tile instanceof StreetTile street)) {
                return MessageFactory.error(lobbyId, "Kein gültiges Mietfeld.");
            }

            Player owner = street.getOwner();
            if (owner == null || owner.getId() == playerId) {
                return MessageFactory.error(lobbyId, "Keine Miete zu zahlen.");
            }

            int rent = street.calculateRent();
            player.transferCash(owner, rent);

            // Falls Spieler jetzt bankrott ist
            if (!player.isAlive()) {
                extraMessages.add(MessageFactory.playerLost(lobbyId, playerId));
            }

            gameState.advanceTurn();
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Miete zahlen: " + e.getMessage());
        }
    }
}
