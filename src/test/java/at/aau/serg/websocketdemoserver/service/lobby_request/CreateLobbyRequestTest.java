package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateLobbyRequestTest {

    private LobbyManager lobbyManager;
    private CreateLobbyRequest request;

    @BeforeEach
    void setUp() {
        lobbyManager = mock(LobbyManager.class);
        request = new CreateLobbyRequest(lobbyManager);
    }

    @Test
    void testHandle_createsLobbySuccessfully() {
        LobbyMessage message = new LobbyMessage(0, LobbyMessageType.CREATE_LOBBY,
                Map.of("lobbyName", "TestLobby"));

        when(lobbyManager.createLobby("TestLobby")).thenReturn(42);

        List<LobbyMessage> response = request.handle(message);

        assertEquals(1, response.size());
        LobbyMessage result = response.get(0);
        assertEquals(LobbyMessageType.LOBBY_CREATED, result.getType());
        assertEquals(42, result.getPayload());
    }

    @Test
    void testHandle_withInvalidPayload_returnsError() {
        LobbyMessage message = new LobbyMessage(0, LobbyMessageType.CREATE_LOBBY, "invalid");

        List<LobbyMessage> response = request.handle(message);

        assertEquals(1, response.size());
        LobbyMessage result = response.get(0);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Error creating lobby"));
    }
}
