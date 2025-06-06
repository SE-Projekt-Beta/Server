package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartGameRequestTest {

    @Test
    void testHandle_validStart() {
        // Mock Setup
        LobbyManager lobbyManager = mock(LobbyManager.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        StartGameRequest request = new StartGameRequest(lobbyManager, messagingTemplate);

        // Lobby mit 2 Spielern
        Lobby lobby = new Lobby("TestLobby");
        lobby.addPlayer(new PlayerDTO(1, "Alice"));
        lobby.addPlayer(new PlayerDTO(2, "Bob"));
        when(lobbyManager.getLobby(1)).thenReturn(lobby);

        LobbyMessage message = new LobbyMessage(1, LobbyMessageType.START_GAME, null);

        // Methode ausführen
        List<LobbyMessage> result = request.handle(message);

        // Assertions zur Antwort
        assertEquals(2, result.size());
        LobbyMessage response = result.get(0);
        assertEquals(LobbyMessageType.START_GAME, response.getType());

        GameStartPayload payload = (GameStartPayload) response.getPayload();
        assertNotNull(payload);
        assertEquals(2, payload.getPlayerOrder().size());

        // Messaging prüfen
        verify(messagingTemplate, times(2)).convertAndSend(startsWith("/topic/dkt/1"), any(Object.class));

    }

    @Test
    void testHandle_notEnoughPlayers() {
        LobbyManager lobbyManager = mock(LobbyManager.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        StartGameRequest request = new StartGameRequest(lobbyManager, messagingTemplate);

        Lobby lobby = new Lobby("MiniLobby");
        lobby.addPlayer(new PlayerDTO(1, "Solo"));
        when(lobbyManager.getLobby(2)).thenReturn(lobby);

        LobbyMessage message = new LobbyMessage(2, LobbyMessageType.START_GAME, null);
        List<LobbyMessage> result = request.handle(message);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
        assertTrue(result.get(0).getPayload().toString().contains("Nicht genügend Spieler"));
    }

    @Test
    void testHandle_lobbyNotFound() {
        LobbyManager lobbyManager = mock(LobbyManager.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        StartGameRequest request = new StartGameRequest(lobbyManager, messagingTemplate);

        when(lobbyManager.getLobby(99)).thenReturn(null);
        LobbyMessage message = new LobbyMessage(99, LobbyMessageType.START_GAME, null);
        List<LobbyMessage> result = request.handle(message);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
        assertTrue(result.get(0).getPayload().toString().contains("Nicht genügend Spieler"));
    }
}
