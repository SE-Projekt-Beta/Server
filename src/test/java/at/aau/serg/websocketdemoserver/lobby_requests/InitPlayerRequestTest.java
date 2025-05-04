package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.lobby_request.InitPlayerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitPlayerRequestTest {

    private GameState gameState;
    private InitPlayerRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new InitPlayerRequest();
    }

    @Test
    void testInvalidParameter_Null() {
        LobbyMessage result = request.execute(gameState, null);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Spielername.", result.getPayload());
    }

    @Test
    void testInvalidParameter_NotString() {
        LobbyMessage result = request.execute(gameState, 123); // kein String
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Spielername.", result.getPayload());
    }

    @Test
    void testInvalidParameter_BlankString() {
        LobbyMessage result = request.execute(gameState, "   ");
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Spielername.", result.getPayload());
    }

    @Test
    void testValidNickname() {
        LobbyMessage result = request.execute(gameState, "Alice");
        assertEquals(LobbyMessageType.PLAYER_INIT, result.getType());

        String[] payload = (String[]) result.getPayload();
        assertEquals("Alice", payload[1]);
        assertDoesNotThrow(() -> Integer.parseInt(payload[0]));
    }
}
