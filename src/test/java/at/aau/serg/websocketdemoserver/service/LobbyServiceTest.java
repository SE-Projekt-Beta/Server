package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LobbyServiceTest {

    private LobbyService lobbyService;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        lobbyService = new LobbyService(messagingTemplate);
    }

    @Test
    void handleReturnsErrorWhenMessageIsNull() {
        List<LobbyMessage> result = lobbyService.handle(null);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
        assertTrue(result.get(0).getPayload().toString().contains("Missing or invalid"));
    }

    @Test
    void handleReturnsErrorWhenMessageTypeIsNull() {
        LobbyMessage message = new LobbyMessage();
        message.setType(null);

        List<LobbyMessage> result = lobbyService.handle(message);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
        assertTrue(result.get(0).getPayload().toString().contains("Missing or invalid"));
    }

    @Test
    void handleReturnsErrorWhenHandlerNotFound() {
        LobbyMessage message = new LobbyMessage();
        message.setType(LobbyMessageType.LOBBY_UPDATE); // kein Handler dafür registriert

        List<LobbyMessage> result = lobbyService.handle(message);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
        assertTrue(result.get(0).getPayload().toString().contains("No handler"));
    }

}
