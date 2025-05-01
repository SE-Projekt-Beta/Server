package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class PayRentRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            Tile tile = player.getCurrentTile();
            if (!(tile instanceof StreetTile street)) {
                return new GameMessage(MessageType.ERROR, "Kein Mietfeld.");
            }

            Player owner = street.getOwner();
            if (owner == null || owner.getId() == player.getId()) {
                return new GameMessage(MessageType.ERROR, "Keine Miete zu zahlen.");
            }

            int rent = street.calculateRent();
            if (player.getCash() < rent) {
                return new GameMessage(MessageType.ERROR, "Nicht genug Geld für Miete.");
            }

            player.transferCash(owner, rent);

            return new GameMessage(MessageType.MUST_PAY_RENT, Map.of(
                    "playerId", playerId,
                    "ownerId", owner.getId(),
                    "tilePos", street.getIndex(),
                    "tileName", street.getLabel(),
                    "amount", rent
            ));
        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Miete zahlen: " + e.getMessage());
        }
    }
}