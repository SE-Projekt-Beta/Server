package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LobbyServiceTest {

    private LobbyService lobbyService;
    private GameHandler mockGameHandler;
    private GameState gameState;

    @BeforeEach
    void setup() {
        gameState = new GameState();
        mockGameHandler = mock(GameHandler.class);
        lobbyService = new LobbyService(gameState, mockGameHandler);
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
        // PLAYER_INIT ist registriert
        LobbyMessage message = new LobbyMessage(LobbyMessageType.PLAYER_INIT, "Thomas");
        List<Object> result = lobbyService.handle(message);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof LobbyMessage);
    }

    @Test
    void testHandleStartGameAppendsExtraMessages() {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.START_GAME, null);

        GameMessage extraMessage = new GameMessage(MessageType.START_GAME, new GameStartedPayload(List.of()));
        when(mockGameHandler.getExtraMessages()).thenReturn(List.of(extraMessage));

        List<Object> result = lobbyService.handle(message);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof LobbyMessage);
        assertTrue(result.get(1) instanceof GameMessage);
    }
}
