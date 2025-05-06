package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.lobby_request.LeaveLobbyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaveLobbyRequestTest {

    private LeaveLobbyRequest request;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        request = new LeaveLobbyRequest();
        gameState = mock(GameState.class);
    }

    @Test
    void testGetType() {
        assertEquals(LobbyMessageType.LEAVE_LOBBY, request.getType());
    }

    @Test
    void testInvalidParameter_returnsError() {
        LobbyMessage result = request.execute(gameState, "wrong-type");
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültige Spieler-ID.", result.getPayload());
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(gameState.getPlayer(1)).thenReturn(null);
        LobbyMessage result = request.execute(gameState, 1);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Spieler nicht gefunden.", result.getPayload());
    }

    @Test
    void testGameAlreadyStarted_returnsError() {
        Player player = mock(Player.class);
        when(gameState.getPlayer(1)).thenReturn(player);
        when(gameState.isGameStarted()).thenReturn(true);

        LobbyMessage result = request.execute(gameState, 1);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Verlassen während des Spiels nicht möglich.", result.getPayload());
    }

    @Test
    void testLeaveLobby_successfulUpdateReturned() {
        Player playerToRemove = mock(Player.class);
        when(playerToRemove.getId()).thenReturn(1);
        when(playerToRemove.getNickname()).thenReturn("Eva");

        Player remaining = mock(Player.class);
        when(remaining.getId()).thenReturn(2);
        when(remaining.getNickname()).thenReturn("Tom");

        when(gameState.getPlayer(1)).thenReturn(playerToRemove);
        when(gameState.isGameStarted()).thenReturn(false);

        // Nach Entfernen bleibt nur noch "Tom" übrig
        when(gameState.getPlayers()).thenReturn(List.of(remaining));

        LobbyMessage result = request.execute(gameState, 1);

        verify(gameState).removePlayer(playerToRemove);

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.getType());
        assertTrue(result.getPayload() instanceof LobbyUpdatePayload);

        LobbyUpdatePayload payload = (LobbyUpdatePayload) result.getPayload();
        assertEquals(1, payload.getPlayers().size());
        assertEquals("Tom", payload.getPlayers().get(0).getNickname());
        assertEquals(2, payload.getPlayers().get(0).getPlayerId());
    }
}
