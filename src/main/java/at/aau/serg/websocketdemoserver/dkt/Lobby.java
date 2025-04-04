package at.aau.serg.websocketdemoserver.dkt;

import java.util.HashSet;
import java.util.Set;

public class Lobby {
    private final Set<String> players = new HashSet<>();

    public void addPlayer(String playerId) {
        players.add(playerId);
    }

    public Set<String> getPlayers() {
        return new HashSet<>(players);
    }
}
