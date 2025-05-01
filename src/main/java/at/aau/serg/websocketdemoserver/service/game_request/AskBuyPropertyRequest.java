package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

import java.util.Map;

public class AskBuyPropertyRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            int playerId = Integer.parseInt(payload.get("playerId").toString());

            Player player = gameState.getPlayer(playerId);
            if (player == null || player.getCurrentTile() == null) {
                return new GameMessage(MessageType.ERROR, "Ungültiger Spieler oder Spielfeld.");
            }

            Tile tile = player.getCurrentTile();
            if (!(tile instanceof StreetTile street)) {
                return new GameMessage(MessageType.ERROR, "Kein kaufbares Feld.");
            }

            if (street.getOwner() != null) {
                return new GameMessage(MessageType.ERROR, "Feld bereits im Besitz.");
            }

            return new GameMessage(
                    MessageType.CAN_BUY_PROPERTY,
                    Map.of(
                            "playerId", playerId,
                            "tilePos", street.getIndex(),
                            "tileName", street.getLabel(),
                            "price", street.getPrice()
                    )
            );

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler bei Kaufabfrage: " + e.getMessage());
        }
    }
}