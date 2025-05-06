package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.Lobby;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LobbyServiceTest {

    private LobbyService lobbyService;
    private LobbyManager mockLobbyManager;
    private LobbyHandlerInterface initHandler;
    private LobbyHandlerInterface startHandler;
    private GameHandler mockGameHandler;
    private GameState gameState;
    private Lobby mockLobby;

    @BeforeEach
    void setup() {
        mockLobbyManager = mock(LobbyManager.class);
        initHandler      = mock(LobbyHandlerInterface.class);
        startHandler     = mock(LobbyHandlerInterface.class);
        mockGameHandler  = mock(GameHandler.class);

        // Stub out a Lobby that returns our GameState & GameHandler
        mockLobby = mock(Lobby.class);
        gameState = new GameState();
        when(mockLobby.getGameState()).thenReturn(gameState);
        when(mockLobby.getGameHandler()).thenReturn(mockGameHandler);
        when(mockLobbyManager.getLobby(anyString())).thenReturn(mockLobby);

        // Configure our handlers
        when(initHandler.getType()).thenReturn(LobbyMessageType.PLAYER_INIT);
        when(startHandler.getType()).thenReturn(LobbyMessageType.START_GAME);

        // PLAYER_INIT handler echoes back the nickname
        when(initHandler.execute(eq(gameState), any()))
                .thenAnswer(inv -> {
                    Object payload = inv.getArgument(1);
                    return new LobbyMessage(LobbyMessageType.PLAYER_INIT, payload);
                });

        // START_GAME handler returns an empty payload
        when(startHandler.execute(eq(gameState), isNull()))
                .thenReturn(new LobbyMessage(LobbyMessageType.START_GAME, null));

        lobbyService = new LobbyService(
                mockLobbyManager,
                List.of(initHandler, startHandler)
        );
    }

    @Test
    void testHandleNullMessage() {
        List<Object> result = lobbyService.handle(null);
        assertEquals(1, result.size());
        LobbyMessage response = (LobbyMessage) result.get(0);
        assertEquals(LobbyMessageType.ERROR, response.getType());
    }

    @Test
    void testHandleKnownRequest() {
        String lobbyId  = "lobby1";
        String nickname = "Thomas";
        // Use the 3-arg constructor so lobbyId is set on entry
        LobbyMessage message =
                new LobbyMessage(LobbyMessageType.PLAYER_INIT, lobbyId, nickname);

        List<Object> result = lobbyService.handle(message);

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof LobbyMessage);

        LobbyMessage response = (LobbyMessage) result.get(0);
        assertEquals(LobbyMessageType.PLAYER_INIT, response.getType());
        assertEquals(lobbyId, response.getLobbyId());
        assertEquals(nickname, response.getPayload());

        // Verify that our initHandler was called with the Lobby's GameState
        verify(initHandler).execute(gameState, nickname);
    }

    @Test
    void testHandleStartGameAppendsExtraMessages() {
        String lobbyId = "lobby2";
        LobbyMessage message =
                new LobbyMessage(LobbyMessageType.START_GAME, lobbyId, null);

        // Stub one extra GameMessage in the Lobby's GameHandler
        GameMessage extraMessage =
                new GameMessage(MessageType.START_GAME, new GameStartedPayload(List.of()));
        when(mockGameHandler.getExtraMessages())
                .thenReturn(List.of(extraMessage));

        List<Object> result = lobbyService.handle(message);

        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof LobbyMessage);
        assertTrue(result.get(1) instanceof GameMessage);

        LobbyMessage first = (LobbyMessage) result.get(0);
        assertEquals(LobbyMessageType.START_GAME, first.getType());
        assertEquals(lobbyId, first.getLobbyId());

        GameMessage second = (GameMessage) result.get(1);
        assertSame(extraMessage, second);
    }
}
