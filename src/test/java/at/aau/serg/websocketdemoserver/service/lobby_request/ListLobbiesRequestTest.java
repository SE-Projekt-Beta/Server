package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.Lobby;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListLobbiesRequestTest {

    private LobbyManager lobbyManager;
    private ListLobbiesRequest request;

    @BeforeEach
    void setUp() {
        lobbyManager = mock(LobbyManager.class);
        request = new ListLobbiesRequest(lobbyManager);
    }

    @Test
    void testHandle_withNullLobby() {
        when(lobbyManager.getLobbyIds()).thenReturn(List.of(2));
        when(lobbyManager.getLobby(2)).thenReturn(null);

        LobbyMessage dummyMessage = new LobbyMessage();
        List<LobbyMessage> result = request.handle(dummyMessage);

        assertEquals(1, result.size());
        LobbyMessage msg = result.get(0);
        assertEquals(LobbyMessageType.LOBBY_LIST, msg.getType());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) msg.getPayload();
        assertEquals(1, list.size());

        Map<String, Object> lobbyData = list.get(0);
        assertEquals(2, lobbyData.get("lobbyId"));
        assertEquals("Unknown", lobbyData.get("lobbyName"));
        assertEquals(0, lobbyData.get("playerCount"));
    }
}
