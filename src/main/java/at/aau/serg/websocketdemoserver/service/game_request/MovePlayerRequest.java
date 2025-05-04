package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.MovePlayerPayload;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class MovePlayerRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        int playerId = gameState.getCurrentPlayer().getId();
        Player player = gameState.getPlayer(playerId);

        int dice = message.parsePayload(Integer.class);  // einfacher Payload: gewürfelte Zahl
        int newIndex = player.calculateNewPosition(dice);

        // Bewegung durchführen
        player.moveToTile(newIndex, GameBoard.get());
        Tile target = player.getCurrentTile();

        // Antwort vorbereiten
        MovePlayerPayload payload = new MovePlayerPayload();
        payload.setPlayerId(player.getId());
        payload.setPos(newIndex);
        payload.setDice(dice);
        payload.setTileName(target.getLabel());
        payload.setTileType(target.getType().name());

        return new GameMessage(MessageType.PLAYER_MOVED, payload);
    }
}
