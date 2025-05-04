package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLobbyEntry;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.lobby_request.StartGameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartGameRequestTest {

    private GameState gameState;
    private GameHandler gameHandler;
    private StartGameRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        gameHandler = mock(GameHandler.class);
        request = new StartGameRequest(gameHandler);
    }

    @Test
    void testExecuteNotEnoughPlayers() {
        LobbyMessage result = request.execute(gameState, null);

        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Mindestens 2 Spieler notwendig, um das Spiel zu starten.", result.getPayload());
        verifyNoInteractions(gameHandler);
    }

    @Test
    void testExecuteEnoughPlayers() {
        gameState.addPlayer("Alice");
        gameState.addPlayer("Bob");

        LobbyMessage result = request.execute(gameState, null);

        assertEquals(LobbyMessageType.START_GAME, result.getType());

        @SuppressWarnings("unchecked")
        List<PlayerLobbyEntry> payload = (List<PlayerLobbyEntry>) result.getPayload();

        assertEquals(2, payload.size());
        assertEquals("Alice", payload.get(0).getNickname());
        assertEquals("Bob", payload.get(1).getNickname());

        verify(gameHandler, times(1)).initGame(gameState.getPlayers());
    }
}
