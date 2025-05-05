package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.Lobby;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyManager {
    private final ConcurrentHashMap<String, Lobby> lobbies = new ConcurrentHashMap<>();

    /** Create and register a new lobby */
    public Lobby createLobby(String name) {
        Lobby lobby = new Lobby(name);
        lobbies.put(lobby.getId(), lobby);
        return lobby;
    }

    /** Lookup by ID */
    public Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    /** Remove a lobby entirely */
    public void removeLobby(String lobbyId) {
        lobbies.remove(lobbyId);
    }

    /** List all active lobbies */
    public Collection<Lobby> listLobbies() {
        return lobbies.values();
    }
}
