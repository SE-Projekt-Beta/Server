package at.aau.serg.websocketdemoserver.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private final Map<String, Integer> playerPositions = new HashMap<>();
    private List<String> playerOrder = new ArrayList<>();
    private int currentPlayerIndex = 0;

    public int getPosition(String playerId) {
        return playerPositions.getOrDefault(playerId, 0);
    }

    public void updatePosition(String playerId, int newPos) {
        playerPositions.put(playerId, newPos);
    }

    public Map<String, Integer> getAllPositions() {
        return new HashMap<>(playerPositions);
    }

    public void setPlayerOrder(List<String> players) {
        this.playerOrder = new ArrayList<>(players);
        this.currentPlayerIndex = 0;
    }

    public String getCurrentPlayer() {
        if (playerOrder.isEmpty()) {
            return null;
        }
        return playerOrder.get(currentPlayerIndex % playerOrder.size());
    }

    public void nextTurn() {
        if (!playerOrder.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % playerOrder.size();
        }
    }
}
