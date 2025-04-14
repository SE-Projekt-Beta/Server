package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Lobby {
    private final List<Player> players = new ArrayList<>();

    public synchronized Player addPlayer(String username) {
        Player player = new Player(username);
        players.add(player);
        return player;
    }

    public synchronized List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public synchronized boolean isReadyToStart() {
        return players.size() >= 2;
    }

    public synchronized void clearLobby() {
        players.clear();
    }
}