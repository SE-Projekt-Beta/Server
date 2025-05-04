package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
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
    void testHandleGameMessage_sendsMainAndExtraMessages() {
        GameMessage input = new GameMessage();
        input.setType(MessageType.ROLL_DICE);

        GameMessage response = new GameMessage();
        response.setType(MessageType.ROLL_DICE);

        GameMessage extra1 = new GameMessage();
        extra1.setType(MessageType.START_MONEY);

        GameMessage extra2 = new GameMessage();
        extra2.setType(MessageType.BUY_PROPERTY);

        when(gameHandler.handle(input)).thenReturn(response);
        when(gameHandler.getExtraMessages()).thenReturn(List.of(extra1, extra2));

        controller.handleGameMessage(input);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<GameMessage> messageCaptor = ArgumentCaptor.forClass(GameMessage.class);

        verify(messagingTemplate, times(3))
                .convertAndSend(destinationCaptor.capture(), messageCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();
        List<GameMessage> messages = messageCaptor.getAllValues();

        assertEquals("/topic/dkt", destinations.get(0));
        assertEquals("/topic/dkt", destinations.get(1));
        assertEquals("/topic/dkt", destinations.get(2));

        assertEquals(response, messages.get(0));
        assertEquals(extra1, messages.get(1));
        assertEquals(extra2, messages.get(2));
    }
}
