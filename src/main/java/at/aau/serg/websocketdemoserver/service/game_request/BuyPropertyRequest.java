package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.BuyPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class BuyPropertyRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        BuyPropertyPayload payload = message.parsePayload(BuyPropertyPayload.class);
        int playerId = payload.getPlayerId();
        int tileIndex = payload.getTilePosition();

        Player player = gameState.getPlayer(playerId);
        if (player == null) {
            return GameMessage.error("Spieler nicht gefunden.");
        }

        if (gameState.getCurrentPlayer().getId() != playerId) {
            return GameMessage.error("Nicht dein Zug.");
        }

        Tile tile = GameBoard.get().getTile(tileIndex);
        if (!(tile instanceof StreetTile streetTile)) {
            return GameMessage.error("Kein kaufbares Grundstück.");
        }

        if (streetTile.getOwner() != null) {
            return GameMessage.error("Grundstück ist bereits verkauft.");
        }

        if (!player.purchaseStreet(tileIndex)) {
            return GameMessage.error("Kauf fehlgeschlagen. Zu wenig Geld?");
        }

        BuyPropertyPayload response = new BuyPropertyPayload(
                playerId,
                tileIndex,
                streetTile.getLabel(),
                streetTile.getPrice()
        );

        return new GameMessage(MessageType.PROPERTY_BOUGHT, response);
    }
}
