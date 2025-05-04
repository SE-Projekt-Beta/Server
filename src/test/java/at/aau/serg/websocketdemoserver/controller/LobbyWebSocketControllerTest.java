package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LobbyWebSocketControllerTest {

    private SimpMessagingTemplate messagingTemplate;
    private LobbyService lobbyService;
    private LobbyWebSocketController controller;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        lobbyService = mock(LobbyService.class);
        controller = new LobbyWebSocketController(messagingTemplate, lobbyService);
    }

    @Test
    void testHandleLobbyMessage_sendsAllResponses() {
        LobbyMessage input = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, "Thomas");

        LobbyMessage response1 = new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, List.of("Thomas"));
        LobbyMessage response2 = new LobbyMessage(LobbyMessageType.PLAYER_INIT, new String[]{"1", "Thomas"});

        when(lobbyService.handle(input)).thenReturn(List.of(response1, response2));

        controller.handleLobbyMessage(input);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LobbyMessage> messageCaptor = ArgumentCaptor.forClass(LobbyMessage.class);

        verify(messagingTemplate, times(2))
                .convertAndSend(destinationCaptor.capture(), messageCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();
        List<LobbyMessage> messages = messageCaptor.getAllValues();

        assertEquals("/topic/lobby", destinations.get(0));
        assertEquals("/topic/lobby", destinations.get(1));

        assertEquals(response1, messages.get(0));
        assertEquals(response2, messages.get(1));
    }
}
