package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestGameStartedTest {

    private RequestGameStarted request;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        request = new RequestGameStarted();
        gameState = new GameState();

        // Spieler hinzufügen
        Player p1 = new Player("A", gameState.getBoard());
        Player p2 = new Player("B", gameState.getBoard());
        gameState.startGame(java.util.List.of(p1, p2));
    }

    @Test
    void testExecute() {
        GameMessage result = request.execute(123, null, gameState, new java.util.ArrayList<>());

        assertEquals(123, result.getLobbyId());
        assertEquals("GAME_STARTED", result.getType().name());

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) result.getPayload();

        assertEquals(2, payload.get("playerCount"));
        assertEquals(gameState.getCurrentRound(), payload.get("currentRound"));
        assertEquals(gameState.getCurrentPlayerId(), payload.get("currentPlayerId"));
    }
}
