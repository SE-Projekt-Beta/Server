package at.aau.serg.websocketdemoserver.model.gamestate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState gameState;
    private GameBoard board;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        board = gameState.getBoard();

        player1 = new Player("Alice", board);
        player2 = new Player("Bob", board);

        // ❗ Neue mutable Liste statt List.of (die wäre immutable)
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        gameState.startGame(players);
    }

    @Test
    void testStartGameInitializesPlayers() {
        assertEquals(2, gameState.getAllPlayers().size());
        assertTrue(gameState.getAllPlayers().contains(player1));
        assertTrue(gameState.getAllPlayers().contains(player2));
    }

    @Test
    void testGetCurrentPlayerAndId() {
        Player current = gameState.getCurrentPlayer();
        assertNotNull(current);
        assertEquals(current.getId(), gameState.getCurrentPlayerId());
    }

    @Test
    void testAdvanceTurnCyclesThroughPlayers() {
        int firstId = gameState.getCurrentPlayerId();
        gameState.advanceTurn();
        int secondId = gameState.getCurrentPlayerId();
        assertNotEquals(firstId, secondId);

        gameState.advanceTurn();
        int backToFirstId = gameState.getCurrentPlayerId();
        assertEquals(firstId, backToFirstId);
    }

    @Test
    void testIsPlayersTurn() {
        int currentId = gameState.getCurrentPlayerId();
        assertTrue(gameState.isPlayersTurn(currentId));
        assertFalse(gameState.isPlayersTurn(currentId + 1000));
    }

    @Test
    void testGetRankingList() {
        List<Player> ranking = gameState.getRankingList();
        assertEquals(2, ranking.size());
        assertTrue(ranking.contains(player1));
        assertTrue(ranking.contains(player2));
    }

    @Test
    void testGameOverAfterMaxRounds() {
        // Manuell viele Runden durchlaufen
        for (int i = 0; i < 10; i++) {
            gameState.advanceTurn();
        }
        assertTrue(gameState.isGameOver(1, true));
        assertFalse(gameState.isGameOver(20, true));
        assertFalse(gameState.isGameOver(1, false)); // Nur wenn Modus aktiv
    }

    @Test
    void testResetGameClearsState() {
        gameState.resetGame();
        assertEquals(0, gameState.getAllPlayers().size());
        assertEquals(1, gameState.getCurrentRound());
    }
}
