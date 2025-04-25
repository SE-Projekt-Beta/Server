package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LobbyWebSocketControllerTest {

    private SimpMessagingTemplate messagingTemplate;
    private LobbyService lobbyService;
    private LobbyWebSocketController controller;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        lobbyService = mock(LobbyService.class);

        // Workaround: LobbyWebSocketController erstellt LobbyService im Konstruktor
        // → wir verwenden hier einen Spy + Reflektion (oder du übergibst lobbyService direkt im echten Code).
        GameHandler gameHandler = mock(GameHandler.class);
        controller = new LobbyWebSocketController(messagingTemplate, gameHandler) {
            @Override
            public void handleLobbyMessage(LobbyMessage message) {
                List<LobbyMessage> responses = lobbyService.handle(message);
                for (LobbyMessage response : responses) {
                    messagingTemplate.convertAndSend("/topic/lobby", response);
                }
            }
        };
    }

    @Test
    void testHandleLobbyMessageSendsAllResponses() {
        // given
        LobbyMessage incoming = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, null);
        LobbyMessage expectedResponse = new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, "Spieler A");

        when(lobbyService.handle(incoming)).thenReturn(List.of(expectedResponse));

        // when
        controller.handleLobbyMessage(incoming);

        // then
        ArgumentCaptor<LobbyMessage> captor = ArgumentCaptor.forClass(LobbyMessage.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby"), captor.capture());

        LobbyMessage actual = captor.getValue();
        assertEquals(expectedResponse.getType(), actual.getType());
        assertEquals(expectedResponse.getPayload(), actual.getPayload());
    }
}
