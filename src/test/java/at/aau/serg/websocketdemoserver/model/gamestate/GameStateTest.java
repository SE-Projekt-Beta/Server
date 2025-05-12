package at.aau.serg.websocketdemoserver.model.gamestate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class GameStateTest {

    private GameState gameState;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setup() {
        gameState = new GameState();
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        player3 = mock(Player.class);

        // Sicherstellen, dass die Spieler eine gültige ID und andere Attribute haben
        when(player1.getId()).thenReturn(1);
        when(player2.getId()).thenReturn(2);
        when(player3.getId()).thenReturn(3);

        when(player1.isAlive()).thenReturn(true);
        when(player2.isAlive()).thenReturn(true);
        when(player3.isAlive()).thenReturn(true);

        when(player1.isSuspended()).thenReturn(false);
        when(player2.isSuspended()).thenReturn(false);
        when(player3.isSuspended()).thenReturn(false);
    }

    @Test
    void testStartGame_shufflesAndRegistersPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        gameState.startGame(players);

        assertNotNull(gameState.getPlayer(player1.getId()));
        assertNotNull(gameState.getPlayer(player2.getId()));
        assertEquals(2, gameState.getAllPlayers().size());
    }

    @Test
    void testGetCurrentPlayer_andAdvanceTurn() {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        gameState.startGame(players);

        Player current = gameState.getCurrentPlayer();
        assertTrue(current == player1 || current == player2);

        gameState.advanceTurn();
        Player next = gameState.getCurrentPlayer();
        assertNotSame(current, next);
    }


    @Test
    void testIsPlayersTurn() {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        gameState.startGame(players);
        int currentId = gameState.getCurrentPlayerId();

        assertTrue(gameState.isPlayersTurn(currentId));
        assertFalse(gameState.isPlayersTurn(currentId + 100));
    }


    @Test
    void testGetRankingList_sortedCorrectly() {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Setze das Cash der Spieler
        when(player1.calculateWealth()).thenReturn(500);
        when(player2.calculateWealth()).thenReturn(1000);

        gameState.startGame(players);

        List<Player> ranking = gameState.getRankingList();
        assertEquals(player2, ranking.get(0));
        assertEquals(player1, ranking.get(1));
    }


    @Test
    void testStartGame_emptyPlayersList() {
        List<Player> players = new ArrayList<>();
        gameState.startGame(players);
        assertTrue(gameState.getAllPlayers().isEmpty(), "Spieler-Liste sollte leer sein");
    }

    @Test
    void testIsGameOver_withoutRoundsEnabled() {
        List<Player> players = new ArrayList<>();
        players.add(player1);

        gameState.startGame(players);

        // Da das Runden-System deaktiviert ist, sollte das Spiel nicht nach Runden enden
        assertFalse(gameState.isGameOver(10, false));
    }

    @Test
    void testAdvanceTurn_withEmptyTurnOrder() {
        // Leere Turn-Order simulieren
        gameState.advanceTurn();
        assertNull(gameState.getCurrentPlayer(), "Kein Spieler sollte aktuell sein");
    }
}
