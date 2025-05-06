package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GameWebSocketControllerTest {

    private SimpMessagingTemplate messagingTemplate;
    private GameHandler gameHandler;
    private GameWebSocketController controller;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        gameHandler = mock(GameHandler.class);
        controller = new GameWebSocketController(messagingTemplate, gameHandler);
    }

    @Test
    void handleGameMessage_sendsMainAndExtraMessages() {
        // Arrange
        GameMessage inputMessage = new GameMessage();
        GameMessage responseMessage = new GameMessage();
        GameMessage extra1 = new GameMessage();
        GameMessage extra2 = new GameMessage();

        when(gameHandler.handle(inputMessage)).thenReturn(responseMessage);
        when(gameHandler.getExtraMessages()).thenReturn(List.of(extra1, extra2));

        // Act
        controller.handleGameMessage(inputMessage);

        // Assert
        verify(messagingTemplate).convertAndSend("/topic/dkt", responseMessage);
        verify(messagingTemplate).convertAndSend("/topic/dkt", extra1);
        verify(messagingTemplate).convertAndSend("/topic/dkt", extra2);
        verifyNoMoreInteractions(messagingTemplate);
    }
}
