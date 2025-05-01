package at.aau.serg.websocketdemoserver.game;

import at.aau.serg.websocketdemoserver.dto.CurrentPlayerPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private GameState state;

    @BeforeEach
    void setup() {
        state = new GameState();
    }

    @Test
    void testAddPlayersAndGetters() {
        CurrentPlayerPayload p1 = new CurrentPlayerPayload(1, "Alice");
        CurrentPlayerPayload p2 = new CurrentPlayerPayload(2, "Bob");

        state.addPlayers(List.of(p1, p2));

        assertEquals(2, state.getAllPlayers().size());
        assertNotNull(state.getCurrentPlayer());
        assertEquals("Alice", state.getCurrentPlayer().getNickname());
    }

    @Test
    void testAdvanceTurnCyclesCorrectly() {
        CurrentPlayerPayload p1 = new CurrentPlayerPayload(1, "Alice");
        CurrentPlayerPayload p2 = new CurrentPlayerPayload(2, "Bob");

        state.addPlayers(List.of(p1, p2));

        assertEquals("Alice", state.getCurrentPlayer().getNickname());
        state.advanceTurn();
        assertEquals("Bob", state.getCurrentPlayer().getNickname());
        state.advanceTurn();
        assertEquals("Alice", state.getCurrentPlayer().getNickname());
    }

    @Test
    void testAdvanceTurnSkipsSuspended() {
        CurrentPlayerPayload p1 = new CurrentPlayerPayload(1, "Alice");
        CurrentPlayerPayload p2 = new CurrentPlayerPayload(2, "Bob");

        state.addPlayers(List.of(p1, p2));

        Player bob = state.getAllPlayers().stream().filter(p -> p.getNickname().equals("Bob")).findFirst().orElseThrow();
        bob.suspendForRounds(1);

        assertEquals("Alice", state.getCurrentPlayer().getNickname());
        state.advanceTurn();
        assertEquals("Alice", state.getCurrentPlayer().getNickname()); // Bob wird übersprungen
    }

    @Test
    void testGetPlayerById() {
        CurrentPlayerPayload p1 = new CurrentPlayerPayload(1, "Charlie");

        state.addPlayers(List.of(p1));
        Player player = state.getPlayer(1);

        assertNotNull(player);
        assertEquals("Charlie", player.getNickname());
    }

    @Test
    void testGetRankingList() {
        CurrentPlayerPayload p1 = new CurrentPlayerPayload(1, "Alpha");
        CurrentPlayerPayload p2 = new CurrentPlayerPayload(2, "Beta");

        state.addPlayers(List.of(p1, p2));

        Player alpha = state.getAllPlayers().stream().filter(p -> p.getNickname().equals("Alpha")).findFirst().orElseThrow();
        Player beta = state.getAllPlayers().stream().filter(p -> p.getNickname().equals("Beta")).findFirst().orElseThrow();

        // Alpha hat mehr Geld → sollte oben stehen
        alpha.setCash(5000);
        beta.setCash(1000);

        List<Player> ranking = state.getRankingList();

        assertEquals("Alpha", ranking.get(0).getNickname());
        assertEquals("Beta", ranking.get(1).getNickname());
    }

    @Test
    void testIsGameOverRoundsModeEnabled() {
        Player.resetIdCounter();
        state.addPlayers(List.of(
                new CurrentPlayerPayload(1, "Alice"),
                new CurrentPlayerPayload(2, "Bob")
        ));

        // Anfangs: Runde 1
        assertFalse(state.isGameOver(1, true));

        // Simuliere genug Turns, damit eine neue Runde beginnt
        state.advanceTurn(); // Bob dran
        state.advanceTurn(); // wieder Alice, jetzt Runde 2

        assertTrue(state.isGameOver(1, true)); // maxRounds = 1 → Runde > 1 = Spiel vorbei
    }


    @Test
    void testIsGameOverRoundsModeDisabled() {
        assertFalse(state.isGameOver(1, false));
    }

    @Test
    void testResetGame() {
        CurrentPlayerPayload p1 = new CurrentPlayerPayload(1, "Alice");
        state.addPlayers(List.of(p1));
        state.advanceTurn();

        state.resetGame();

        assertTrue(state.getAllPlayers().isEmpty());
        assertEquals(1, state.getCurrentRound());
        assertNull(state.getCurrentPlayer());
    }
}