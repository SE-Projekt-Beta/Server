package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

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
    void handleLobbyMessage_sendsLobbyAndGameMessages() {
        // Arrange
        LobbyMessage input = new LobbyMessage();
        LobbyMessage lobbyResponse = new LobbyMessage();
        GameMessage gameResponse = new GameMessage();

        when(lobbyService.handle(input)).thenReturn(List.of(lobbyResponse, gameResponse));

        // Act
        controller.handleLobbyMessage(input);

        // Assert
        verify(messagingTemplate).convertAndSend("/topic/lobby", lobbyResponse);
        verify(messagingTemplate).convertAndSend("/topic/dkt", gameResponse);
        verifyNoMoreInteractions(messagingTemplate);
    }
}
