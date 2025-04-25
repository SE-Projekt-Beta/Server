package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

public class GameWebSocketControllerTest {

    private GameWebSocketController controller;
    private SimpMessagingTemplate messagingTemplate;
    private GameHandler gameHandler;

    @BeforeEach
    void setup() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        gameHandler = mock(GameHandler.class);
        controller = new GameWebSocketController(messagingTemplate, gameHandler);
    }

    @Test
    void testHandleGameMessageSendsResponse() {
        GameMessage incoming = new GameMessage(MessageType.ROLL_DICE, "{}");
        GameMessage result = new GameMessage(MessageType.PLAYER_MOVED, null);
        when(gameHandler.handle(incoming)).thenReturn(result);
        when(gameHandler.getExtraMessages()).thenReturn(java.util.List.of());

        controller.handleGameMessage(incoming);

        verify(messagingTemplate).convertAndSend("/topic/dkt", result);
    }

    @Test
    void testHandleGameMessageWithExtraMessages() {
        GameMessage incoming = new GameMessage(MessageType.ROLL_DICE, "{}");
        GameMessage result = new GameMessage(MessageType.PLAYER_MOVED, null);
        GameMessage extra = new GameMessage(MessageType.CAN_BUY_PROPERTY, null);

        when(gameHandler.handle(incoming)).thenReturn(result);
        when(gameHandler.getExtraMessages()).thenReturn(java.util.List.of(extra));

        controller.handleGameMessage(incoming);

        verify(messagingTemplate).convertAndSend("/topic/dkt", result);
        verify(messagingTemplate).convertAndSend("/topic/dkt", extra);
    }
}
