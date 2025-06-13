package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PayPrisonRequestTest {
    private GameState gameState;
    private Player player;
    private PayPrisonRequest request;
    private int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();
        player = new Player("TestPlayer", board);
        gameState.startGame(List.of(player));
        request = new PayPrisonRequest();
    }

    @Test
    void testSuccessfulPayment() {
        player.setCash(100);
        player.setSuspensionRounds(2);
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(50, player.getCash());
        assertEquals(0, player.getSuspensionRounds());
    }

    @Test
    void testNotEnoughMoney() {
        player.setCash(40);
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Nicht genug Geld"));
    }

    @Test
    void testInvalidPlayer() {
        Map<String, Object> payload = Map.of("playerId", 999);
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testExceptionHandling() {
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, "invalid", gameState, extras);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Kaufen"));
    }
}

