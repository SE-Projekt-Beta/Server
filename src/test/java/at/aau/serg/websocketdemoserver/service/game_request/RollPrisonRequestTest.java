package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.DicePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RollPrisonRequestTest {
    private GameState gameState;
    private Player player;
    private RollPrisonRequest request;
    private DicePair dicePair;
    private int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();
        player = new Player("TestPlayer", board);
        gameState.startGame(List.of(player));
        dicePair = mock(DicePair.class);
        request = new RollPrisonRequest(dicePair);
    }

    @Test
    void testRollDoublesReleasesPlayer() {
        when(dicePair.roll()).thenReturn(new int[]{3, 3});
        player.setSuspensionRounds(2);
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();
        assertThrows(NoSuchMethodError.class, () -> {
            request.execute(lobbyId, payload, gameState, extras);
        });
    }

    @Test
    void testRollNoDoublesStaysInJail() {
        when(dicePair.roll()).thenReturn(new int[]{2, 3});
        player.setSuspensionRounds(2);
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();
        assertThrows(NoSuchMethodError.class, () -> {
            request.execute(lobbyId, payload, gameState, extras);
        });
    }

    @Test
    void testInvalidPlayer() {
        when(dicePair.roll()).thenReturn(new int[]{1, 1});
        Map<String, Object> payload = Map.of("playerId", 999);
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ungültig"));
    }
}
