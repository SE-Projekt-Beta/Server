package at.aau.serg.websocketdemoserver.dkt.lobby;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Lobby {

    private final List<String> players = new ArrayList<>();
    private final AtomicInteger playerCounter = new AtomicInteger(1);

    public synchronized String addPlayer() {
        String playerName = "Player" + playerCounter.getAndIncrement();
        players.add(playerName);
        return playerName;
    }

    public synchronized List<String> getPlayers() {
        return new ArrayList<>(players);
    }

    public synchronized JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("players", new JSONArray(players));
        return obj;
    }

    public synchronized boolean isFull() {
        return players.size() >= 2; // oder beliebige Anzahl (z.B. 4)
    }

    public synchronized void clear() {
        players.clear();
        playerCounter.set(1);
    }
}
