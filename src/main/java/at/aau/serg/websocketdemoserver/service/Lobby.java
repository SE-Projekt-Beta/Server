package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Lobby {

    private final List<Player> players = new ArrayList<>();
    private final AtomicInteger playerCounter = new AtomicInteger(1);

    public synchronized Player addPlayer() {
        String playerName = "Player" + playerCounter.getAndIncrement();
        Player player = new Player(playerName);
        players.add(player);
        return player;
    }

    public synchronized List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public synchronized JSONObject toJson() {
        JSONObject obj = new JSONObject();
        JSONArray playersArray = new JSONArray();
        for (Player player : players) {
            JSONObject playerObj = new JSONObject();
            playerObj.put("id", player.getId());
            playerObj.put("position", player.getPosition());
            playerObj.put("money", player.getMoney());
            playersArray.put(playerObj);
        }
        obj.put("players", playersArray);
        return obj;
    }

    public synchronized boolean isFull() {
        return players.size() >= 2;
    }

    public synchronized void clear() {
        players.clear();
        playerCounter.set(1);
    }
}
