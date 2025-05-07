package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.gamestate.GameState;

import java.util.HashMap;
import java.util.Map;

// GameManager.java
public class GameManager {
    private static final GameManager instance = new GameManager();
    private final Map<Integer, GameHandler> handlers = new HashMap<>();

    private GameManager() {}

    public static GameManager getInstance() {
        return instance;
    }

    public void registerGame(int lobbyId, GameState gameState) {
        GameHandler handler = new GameHandler(gameState);  // neuer Konstruktor nötig
        handlers.put(lobbyId, handler);
    }

    public GameHandler getHandler(int lobbyId) {
        return handlers.get(lobbyId);
    }

    public void removeGame(int lobbyId) {
        handlers.remove(lobbyId);
    }
}
