package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.AskBuyPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class AskBuyPropertyRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        AskBuyPropertyPayload payload = message.parsePayload(AskBuyPropertyPayload.class);
        int playerId = payload.getPlayerId();

        if (!gameState.isGameStarted()) {
            return GameMessage.error("Spiel ist noch nicht gestartet.");
        }

        Player player = gameState.getPlayer(playerId);
        if (player == null) {
            return GameMessage.error("Spieler nicht gefunden.");
        }

        if (!player.equals(gameState.getCurrentPlayer())) {
            return GameMessage.error("Du bist nicht am Zug.");
        }

        Tile currentTile = player.getCurrentTile();
        if (!(currentTile instanceof StreetTile street)) {
            return GameMessage.error("Kein kaufbares Feld.");
        }

        if (street.getOwner() != null) {
            return GameMessage.error("Feld gehört bereits einem Spieler.");
        }

        AskBuyPropertyPayload response = new AskBuyPropertyPayload(
                playerId,
                street.getIndex(),
                street.getLabel(),
                street.getPrice()
        );

        return new GameMessage(MessageType.CAN_BUY_PROPERTY, response);
    }
}
