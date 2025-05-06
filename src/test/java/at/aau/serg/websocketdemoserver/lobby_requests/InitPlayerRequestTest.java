package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.InitPlayerPayload;
import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.lobby_request.InitPlayerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InitPlayerRequestTest {

    private InitPlayerRequest request;
    private GameState gameState;
    private Tile startTile;

    @BeforeEach
    void setUp() {
        request = new InitPlayerRequest();
        gameState = mock(GameState.class);
        startTile = mock(Tile.class);

        // Statisches GameBoard-Mock simulieren
        GameBoard board = mock(GameBoard.class);
        when(board.getTile(1)).thenReturn(startTile);
        GameBoard.setInstance(board);  // Nur falls du Singleton setzt (falls nötig)
    }

    @Test
    void testGetType() {
        assertEquals(LobbyMessageType.PLAYER_INIT, request.getType());
    }

    @Test
    void testParameterNotAString_returnsError() {
        LobbyMessage result = request.execute(gameState, 123); // Integer statt String
        assertEquals(LobbyMessageType.ERROR, result.getType());
    }

    @Test
    void testEmptyNickname_returnsError() {
        LobbyMessage result = request.execute(gameState, "   ");
        assertEquals(LobbyMessageType.ERROR, result.getType());
    }

    @Test
    void testValidNickname_returnsInitMessage() {
        LobbyMessage result = request.execute(gameState, "Eva");

        assertEquals(LobbyMessageType.PLAYER_INIT, result.getType());
        assertTrue(result.getPayload() instanceof InitPlayerPayload);

        InitPlayerPayload payload = (InitPlayerPayload) result.getPayload();
        assertEquals("Eva", payload.getNickname());
        assertTrue(payload.getPlayerId() > 0); // ID wird inkrementiert

        // Prüfen, ob Spieler dem GameState hinzugefügt wurde
        verify(gameState).addPlayer(any(Player.class));
    }
}
