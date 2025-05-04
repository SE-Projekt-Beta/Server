package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

public class InitPlayerRequest implements LobbyHandlerInterface {

    private static int nextId = 1;

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        if (!(parameter instanceof String nickname) || nickname.isBlank()) {
            return new LobbyMessage(LobbyMessageType.ERROR, "Ungültiger Spielername.");
        }

        // Erstelle neuen Spieler mit eindeutiger ID und Start-Position (Index 1)
        Tile startTile = GameBoard.get().getTile(1);
        Player newPlayer = new Player(nextId++, nickname, startTile);

        gameState.addPlayer(newPlayer);

        // Rückgabe: Spieler-ID + Nickname als String-Array
        String[] payload = new String[] {
                String.valueOf(newPlayer.getId()),
                newPlayer.getNickname()
        };

        return new LobbyMessage(LobbyMessageType.PLAYER_INIT, payload);
    }
}
