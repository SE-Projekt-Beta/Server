package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {

    private GameManager manager;

    @BeforeEach
    void setUp() {
        manager = GameManager.getInstance();
        manager.reset(); // sicherstellen, dass der Zustand leer ist
    }

    @Test
    void testSingletonInstance() {
        GameManager instance1 = GameManager.getInstance();
        GameManager instance2 = GameManager.getInstance();
        assertSame(instance1, instance2);
    }


    @Test
    void testGetHandlerReturnsNullIfNotRegistered() {
        assertNull(manager.getHandler(999));
    }

    @Test
    void testRemoveGame() {
        GameState gameState = new GameState();
        manager.registerGame(42, gameState);

        assertNotNull(manager.getHandler(42));
        manager.removeGame(42);
        assertNull(manager.getHandler(42));
    }

    @Test
    void testResetClearsAllHandlers() {
        manager.registerGame(1, new GameState());
        manager.registerGame(2, new GameState());

        assertNotNull(manager.getHandler(1));
        assertNotNull(manager.getHandler(2));

        manager.reset();

        assertNull(manager.getHandler(1));
        assertNull(manager.getHandler(2));
    }
}
