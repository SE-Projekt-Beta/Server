package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.GameStartedPayload;
import at.aau.serg.websocketdemoserver.model.Lobby;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameWebSocketControllerTest {

    private GameWebSocketController controller;
    private SimpMessagingTemplate mockTemplate;
    private LobbyManager mockLobbyManager;
    private Lobby mockLobby;
    private GameHandler mockGameHandler;

    @BeforeEach
    void setUp() {
        mockTemplate     = mock(SimpMessagingTemplate.class);
        mockLobbyManager = mock(LobbyManager.class);
        controller       = new GameWebSocketController(mockTemplate, mockLobbyManager);

        // Prepare a stub Lobby with its own GameHandler
        mockLobby        = mock(Lobby.class);
        mockGameHandler = mock(GameHandler.class);
        when(mockLobby.getGameHandler()).thenReturn(mockGameHandler);
        when(mockLobbyManager.getLobby("lobby-1")).thenReturn(mockLobby);
    }

    @Test
    void testHandleGameMessage_BroadcastsPrimaryAndExtras() {
        String lobbyId = "lobby-1";

        // Incoming message
        GameMessage incoming = new GameMessage(MessageType.START_GAME, null);

        // Stub the GameHandler to return a primary and an extra
        GameMessage primary = new GameMessage(MessageType.START_GAME, new GameStartedPayload(List.of()));
        GameMessage extra   = new GameMessage(MessageType.MOVE_PLAYER, List.of());
        when(mockGameHandler.handle(incoming)).thenReturn(primary);
        when(mockGameHandler.getExtraMessages()).thenReturn(List.of(extra));

        // Call the controller
        controller.handleGameMessage(lobbyId, incoming);

        // Capture convertAndSend calls
        ArgumentCaptor<String> destCaptor    = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockTemplate, times(2))
                .convertAndSend(destCaptor.capture(), payloadCaptor.capture());

        // Both sends target the lobby-specific topic
        List<String> destinations = destCaptor.getAllValues();
        assertTrue(destinations.stream()
                .allMatch(d -> d.equals("/topic/lobby/" + lobbyId + "/game")));

        // First payload is the primary, second is the extra
        List<Object> sent = payloadCaptor.getAllValues();
        assertSame(primary, sent.get(0));
        assertSame(extra,   sent.get(1));
    }

    @Test
    void testHandleGameMessage_InvalidLobby_NoSend() {
        // No stub for "missing" lobby → getLobby returns null
        controller.handleGameMessage("missing", new GameMessage(MessageType.MOVE_PLAYER, null));
        verifyNoInteractions(mockTemplate);
    }
}
