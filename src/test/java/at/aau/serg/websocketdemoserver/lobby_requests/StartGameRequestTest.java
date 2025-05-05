package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.lobby_request.StartGameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartGameRequestTest {

    private StartGameRequest request;
    private GameHandler gameHandler;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameHandler = mock(GameHandler.class);
        request = new StartGameRequest(gameHandler);
        gameState = mock(GameState.class);
    }

    @Test
    void testGameNotReady_returnsError() {
        when(gameState.isReadyToStart()).thenReturn(false);

        LobbyMessage result = request.execute(gameState, null);

        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Mindestens 2 Spieler notwendig, um das Spiel zu starten.", result.getPayload());
    }

    @Test
    void testStartGame_success() {
        Player p1 = mock(Player.class);
        when(p1.getId()).thenReturn(1);
        when(p1.getNickname()).thenReturn("Eva");

        Player p2 = mock(Player.class);
        when(p2.getId()).thenReturn(2);
        when(p2.getNickname()).thenReturn("Tom");

        List<Player> players = List.of(p1, p2);

        when(gameState.isReadyToStart()).thenReturn(true);
        when(gameState.getPlayers()).thenReturn(players);

        LobbyMessage result = request.execute(gameState, null);

        verify(gameState).startGame();
        verify(gameHandler).initGame(players);

        assertEquals(LobbyMessageType.START_GAME, result.getType());
        assertTrue(result.getPayload() instanceof GameStartedPayload);

        GameStartedPayload payload = (GameStartedPayload) result.getPayload();
        assertEquals(2, payload.getPlayerOrder().size());

        assertEquals("Eva", payload.getPlayerOrder().get(0).getNickname());
        assertEquals(1, payload.getPlayerOrder().get(0).getPlayerId());

        assertEquals("Tom", payload.getPlayerOrder().get(1).getNickname());
        assertEquals(2, payload.getPlayerOrder().get(1).getPlayerId());
    }
}
