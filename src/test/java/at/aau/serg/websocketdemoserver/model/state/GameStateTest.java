package at.aau.serg.websocketdemoserver.model.state;

import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameBoard board;
    private GameState gameState;

    @BeforeEach
    void setup() {
        board = new GameBoard();
        gameState = new GameState(board);
    }

    @Test
    void testAddPlayerByName() {
        Player p = gameState.addPlayer("Max");
        assertNotNull(p);
        assertEquals("Max", p.getNickname());
        assertEquals(1, gameState.getPlayers().size());
    }

    @Test
    void testAddPlayerByObject() {
        Player p = new Player("Eva", board);
        gameState.addPlayer(p);
        assertTrue(gameState.getPlayers().contains(p));
    }

    @Test
    void testRemovePlayerByObject() {
        Player p = gameState.addPlayer("Lukas");
        gameState.removePlayer(p);
        assertFalse(gameState.getPlayers().contains(p));
    }

    @Test
    void testRemovePlayerById() {
        Player p = gameState.addPlayer("Sara");
        gameState.removePlayer(p.getId());
        assertFalse(gameState.getPlayers().contains(p));
    }

    @Test
    void testGetPlayersUnmodifiable() {
        gameState.addPlayer("Anna");
        List<Player> players = gameState.getPlayers();
        assertThrows(UnsupportedOperationException.class, () -> players.add(new Player("Hack", board)));
    }

    @Test
    void testIsReadyToStart() {
        assertFalse(gameState.isReadyToStart());
        gameState.addPlayer("P1");
        gameState.addPlayer("P2");
        assertTrue(gameState.isReadyToStart());
    }

    @Test
    void testGetCurrentPlayer() {
        Player p1 = gameState.addPlayer("X");
        assertEquals(p1, gameState.getCurrentPlayer());
    }

    @Test
    void testAdvanceTurnAndRound() {
        gameState.addPlayer("A");
        gameState.addPlayer("B");
        gameState.addPlayer("C");

        assertEquals(1, gameState.getCurrentRound());
        gameState.advanceTurn(); // -> B
        gameState.advanceTurn(); // -> C
        gameState.advanceTurn(); // -> A → new round
        assertEquals(2, gameState.getCurrentRound());
    }

    @Test
    void testStartGameSetsRound() {
        gameState.addPlayer("A");
        gameState.addPlayer("B");
        gameState.startGame();
        assertEquals(1, gameState.getCurrentRound());
    }

    @Test
    void testGetBoard() {
        assertEquals(board, gameState.getBoard());
    }

    @Test
    void testSetPlayers() {
        Player p1 = new Player("A", board);
        Player p2 = new Player("B", board);
        gameState.setPlayers(List.of(p1, p2));

        assertEquals(2, gameState.getPlayers().size());
        assertTrue(gameState.getPlayers().contains(p2));
    }

    @Test
    void testGetPlayerById() {
        Player p = gameState.addPlayer("P");
        Player found = gameState.getPlayer(p.getId());
        assertEquals(p, found);
    }

    @Test
    void testGetPlayerById_NotFound() {
        assertNull(gameState.getPlayer(999));
    }

    @Test
    void testGetAllPlayersReturnsCopy() {
        gameState.addPlayer("A");
        List<Player> all = gameState.getAllPlayers();
        assertEquals(1, all.size());
        assertNotSame(all, gameState.getPlayers()); // Copy check
    }


    @Test
    void testGetRankingList() {
        Player p1 = new Player("A", board);
        p1.setCash(500);
        Player p2 = new Player("B", board);
        p2.setCash(1000);
        gameState.setPlayers(List.of(p1, p2));

        List<Player> ranking = gameState.getRankingList();
        assertEquals(p2, ranking.get(0));
        assertEquals(p1, ranking.get(1));
    }
}

