package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.PlayerDTO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lobby {

    private final List<PlayerDTO> players = new ArrayList<>();

    @Getter
    private final String lobbyName;

    public Lobby(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public synchronized PlayerDTO addPlayer(String nickname) {
        for (PlayerDTO player : players) {
            if (player.getNickname().equalsIgnoreCase(nickname)) {
                return player; // Player already exists
            }
        }
        int newId = players.size() + 1; // Simple ID generation
        PlayerDTO newPlayer = new PlayerDTO(newId, nickname);
        players.add(newPlayer);
        return newPlayer;
    }

    public synchronized List<PlayerDTO> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public synchronized boolean isReadyToStart() {
        System.out.println("Lobby " + lobbyName + " is ready to start: " + (players.size() >= 2));
        return players.size() >= 2; // At least 2 players required
    }

    public synchronized void clear() {
        players.clear();
    }

    public String getLobbyName() {
        return lobbyName;
    }
}