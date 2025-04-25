package at.aau.serg.websocketdemoserver.gameTest;

import at.aau.serg.websocketdemoserver.model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private GameState state;

    @BeforeEach
    void setup() {
        state = new GameState();
    }

    @Test
    void testInitialPositionIsZero() {
        assertEquals(0, state.getPosition("p1"));
    }

    @Test
    void testUpdateAndRetrievePosition() {
        state.updatePosition("p1", 5);
        assertEquals(5, state.getPosition("p1"));
    }

    @Test
    void testAllPositionsSnapshot() {
        state.updatePosition("a", 1);
        state.updatePosition("b", 2);
        Map<String, Integer> map = state.getAllPositions();
        assertEquals(2, map.size());
        assertEquals(1, map.get("a"));
    }

    @Test
    void testTurnOrderCycling() {
        state.setPlayerOrder(List.of("a", "b", "c"));
        assertEquals("a", state.getCurrentPlayer());
        state.nextTurn();
        assertEquals("b", state.getCurrentPlayer());
        state.nextTurn();
        assertEquals("c", state.getCurrentPlayer());
        state.nextTurn();
        assertEquals("a", state.getCurrentPlayer());
    }

    @Test
    void testEmptyOrderReturnsNull() {
        assertNull(state.getCurrentPlayer());
    }
}
