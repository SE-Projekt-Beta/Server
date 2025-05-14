package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RequestGameStartedTest {

    private GameState gameState;
    private RequestGameStarted request;
    private int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();

        Player player1 = new Player(1, "Alice", gameState.getBoard());
        Player player2 = new Player(2, "Bob", gameState.getBoard());

        List<Player> players = new ArrayList<>(List.of(player1, player2));
        gameState.startGame(players);

        request = new RequestGameStarted();
    }

    @Test
    void testExecuteReturnsCorrectGameMessage() {
        GameMessage message = request.execute(lobbyId, null, gameState, new ArrayList<>());

        assertNotNull(message);
        assertEquals(lobbyId, message.getLobbyId());
        assertEquals(MessageType.GAME_STARTED, message.getType());
        assertTrue(message.getPayload() instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) message.getPayload();

        assertEquals(gameState.getCurrentRound(), payload.get("currentRound"));
        assertEquals(gameState.getAllPlayers().size(), payload.get("playerCount"));
        assertEquals(gameState.getCurrentPlayerId(), payload.get("currentPlayerId"));
    }
}
