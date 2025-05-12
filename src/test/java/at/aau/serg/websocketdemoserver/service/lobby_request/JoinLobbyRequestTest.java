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
    void testHandle_successfulJoin() {
        int lobbyId = 1;
        int playerId = 99;
        PlayerDTO playerDTO = new PlayerDTO(playerId, "Tester");

        Lobby lobby = mock(Lobby.class);
        List<PlayerDTO> players = List.of(playerDTO);
        when(userManager.getPlayer(playerId)).thenReturn(playerDTO);
        when(lobbyManager.getLobby(lobbyId)).thenReturn(lobby);
        when(lobby.getPlayers()).thenReturn(players);
        when(lobbyManager.getLobbyIds()).thenReturn(List.of(lobbyId));
        when(lobby.getLobbyName()).thenReturn("LobbyOne");

        doNothing().when(lobby).addPlayer(playerDTO);

        // Simuliere JoinLobbyPayload als Map
        Map<String, Object> payloadMap = Map.of("lobbyId", lobbyId, "playerId", playerId);
        LobbyMessage message = new LobbyMessage(lobbyId, LobbyMessageType.JOIN_LOBBY, payloadMap);

        List<LobbyMessage> result = request.handle(message);

        assertEquals(2, result.size());
        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.get(0).getType());
        assertEquals(LobbyMessageType.LOBBY_LIST, result.get(1).getType());
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
