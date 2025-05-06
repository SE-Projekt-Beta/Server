package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

public class InitPlayerRequest implements LobbyHandlerInterface {

    private static int nextId = 1;

    @Override
    public LobbyMessageType getType() {
        return LobbyMessageType.PLAYER_INIT;
    }

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        if (!(parameter instanceof String nickname) || nickname.isBlank()) {
            return new LobbyMessage(LobbyMessageType.ERROR, "Ungültiger Spielername.");
        }

        Tile startTile = GameBoard.get().getTile(1);
        Player newPlayer = new Player(nextId++, nickname, startTile);
        gameState.addPlayer(newPlayer);

        InitPlayerPayload payload = new InitPlayerPayload(newPlayer.getId(), newPlayer.getNickname());
        return new LobbyMessage(LobbyMessageType.PLAYER_INIT, payload);
    }
}
