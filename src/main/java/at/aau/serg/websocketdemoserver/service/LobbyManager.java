package at.aau.serg.websocketdemoserver.service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyManager {
    private final Map<Integer, Lobby> lobbies = new HashMap<>();
    private final AtomicInteger lobbyIdCounter = new AtomicInteger(1);

    public synchronized int createLobby() {
        int id = lobbyIdCounter.getAndIncrement();
        lobbies.put(id, new Lobby());
        return id;
    }

    public synchronized List<Integer> getLobbyIds() {
        return new ArrayList<>(lobbies.keySet());
    }

    public synchronized Lobby getLobby(int id) {
        return lobbies.get(id);
    }

    public synchronized boolean removeLobby(int id) {
        return lobbies.remove(id) != null;
    }
}