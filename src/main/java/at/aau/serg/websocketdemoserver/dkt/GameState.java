package at.aau.serg.websocketdemoserver.dkt;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    private final Map<String, Integer> playerPositions = new HashMap<>();
    private final Map<String, Boolean> inJail = new HashMap<>();

    public int getPosition(String playerId) {
        return playerPositions.getOrDefault(playerId, 0);
    }

    public void updatePosition(String playerId, int newPos) {
        playerPositions.put(playerId, newPos);
    }

    public boolean isInJail(String playerId) {
        return inJail.getOrDefault(playerId, false);
    }

    public void setInJail(String playerId, boolean jailStatus){
        inJail.put(playerId, jailStatus);
    }
}
