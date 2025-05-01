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

public class BuyPropertyRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");
            int tilePos = obj.getInt("tilePos");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            Tile tile = gameState.getBoard().getTile(tilePos);
            if (!(tile instanceof StreetTile street)) {
                return new GameMessage(MessageType.ERROR, "Kein kaufbares Grundstück.");
            }

            if (street.getOwner() != null) {
                return new GameMessage(MessageType.ERROR, "Grundstück gehört bereits " + street.getOwner().getNickname());
            }

            if (player.getCash() < street.getPrice()) {
                return new GameMessage(MessageType.ERROR, "Nicht genügend Geld für den Kauf.");
            }

            boolean success = player.purchaseStreet(tilePos);
            if (!success) {
                return new GameMessage(MessageType.ERROR, "Kauf fehlgeschlagen.");
            }

            return new GameMessage(MessageType.PROPERTY_BOUGHT, Map.of(
                    "playerId", player.getId(),
                    "tilePos", tilePos,
                    "tileName", street.getLabel(),
                    "price", street.getPrice()
            ));

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Kaufen: " + e.getMessage());
        }
    }
}