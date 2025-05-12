package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.Mockito.*;

class LobbyWebSocketControllerTest {

    @Mock
    private LobbyService lobbyService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LobbyWebSocketController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleGeneral_sendsAllMessagesToGlobalTopic() {
        // Arrange
        LobbyMessage input = new LobbyMessage(LobbyMessageType.CREATE_LOBBY, "Test");
        LobbyMessage response1 = new LobbyMessage(LobbyMessageType.LOBBY_LIST, "L1");
        LobbyMessage response2 = new LobbyMessage(LobbyMessageType.LOBBY_CREATED, "L2");

        when(lobbyService.handle(input)).thenReturn(List.of(response1, response2));

        // Act
        controller.handleGeneral(input);

        // Assert
        verify(lobbyService).handle(input);
        verify(messagingTemplate).convertAndSend("/topic/lobby", response1);
        verify(messagingTemplate).convertAndSend("/topic/lobby", response2);
    }

    @Test
    void handleByLobby_sendsLobbyListGlobally_andOthersToLobbyTopic() {
        // Arrange
        int lobbyId = 42;
        LobbyMessage input = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, "Join");
        LobbyMessage lobbyList = new LobbyMessage(LobbyMessageType.LOBBY_LIST, "List");
        LobbyMessage lobbyUpdate = new LobbyMessage(lobbyId, LobbyMessageType.LOBBY_UPDATE, "Update");

        when(lobbyService.handle(input)).thenReturn(List.of(lobbyList, lobbyUpdate));

        // Act
        controller.handleByLobby(lobbyId, input);

        // Assert
        verify(lobbyService).handle(input);
        verify(messagingTemplate).convertAndSend("/topic/lobby", lobbyList);
        verify(messagingTemplate).convertAndSend("/topic/lobby/" + lobbyId, lobbyUpdate);
    }
}
