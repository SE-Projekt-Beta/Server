package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLobbyEntry;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.lobby_request.LeaveLobbyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeaveLobbyRequestTest {

    private GameState gameState;
    private LeaveLobbyRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new LeaveLobbyRequest();
    }

    @Test
    void testNullNickname() {
        LobbyMessage result = request.execute(gameState, null);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Nickname.", result.getPayload());
    }

    @Test
    void testInvalidType() {
        LobbyMessage result = request.execute(gameState, 123);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Nickname.", result.getPayload());
    }

    @Test
    void testBlankNickname() {
        LobbyMessage result = request.execute(gameState, " ");
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Nickname.", result.getPayload());
    }

    @Test
    void testNicknameNotFound() {
        gameState.addPlayer("Eva");
        LobbyMessage result = request.execute(gameState, "Unbekannt");

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.getType());

        @SuppressWarnings("unchecked")
        List<PlayerLobbyEntry> payload = (List<PlayerLobbyEntry>) result.getPayload();

        assertEquals(1, payload.size());
        assertEquals("Eva", payload.get(0).getNickname());
    }

    @Test
    void testNicknameRemoved() {
        gameState.addPlayer("Eva");
        gameState.addPlayer("Max");

        LobbyMessage result = request.execute(gameState, "Eva");

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.getType());

        @SuppressWarnings("unchecked")
        List<PlayerLobbyEntry> payload = (List<PlayerLobbyEntry>) result.getPayload();

        assertEquals(1, payload.size());
        assertEquals("Max", payload.get(0).getNickname());
    }
}
