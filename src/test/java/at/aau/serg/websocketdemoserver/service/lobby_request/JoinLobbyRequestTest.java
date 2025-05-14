package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JoinLobbyRequestTest {

    private LobbyManager lobbyManager;
    private UserManager userManager;
    private JoinLobbyRequest request;

    @BeforeEach
    void setUp() {
        lobbyManager = mock(LobbyManager.class);
        userManager = mock(UserManager.class);
        request = new JoinLobbyRequest(lobbyManager, userManager);
    }


    @Test
    void testHandle_lobbyNotFound() {
        when(lobbyManager.getLobby(1)).thenReturn(null);
        Map<String, Object> payload = Map.of("lobbyId", 1, "playerId", 2);
        LobbyMessage message = new LobbyMessage(1, LobbyMessageType.JOIN_LOBBY, payload);

        List<LobbyMessage> result = request.handle(message);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }

    @Test
    void testHandle_invalidPayload() {
        LobbyMessage message = new LobbyMessage(1, LobbyMessageType.JOIN_LOBBY, "invalid");

        List<LobbyMessage> result = request.handle(message);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }
}
