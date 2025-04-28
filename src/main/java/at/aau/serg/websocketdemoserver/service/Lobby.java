package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.PlayerDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lobby {

    private final List<PlayerDTO> players = new ArrayList<>();

    public synchronized PlayerDTO addPlayer(String nickname) {
        for (PlayerDTO player : players) {
            if (player.getNickname().equalsIgnoreCase(nickname)) {
                return player; // Spieler existiert schon
            }
        }
        int newId = players.size() + 1; // einfache ID-Generierung
        PlayerDTO newPlayer = new PlayerDTO(newId, nickname);
        players.add(newPlayer);
        return newPlayer;
    }

    public synchronized List<PlayerDTO> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public synchronized boolean isReadyToStart() {
        return players.size() >= 2; // Mindestens 2 Spieler nötig
    }

    public synchronized void clear() {
        players.clear();
    }
}
