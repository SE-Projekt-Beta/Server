package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final List<Player> players = new ArrayList<>();

    public synchronized Player addPlayer(String nickname) {
        for (Player p : players) {
            if (p.getNickname().equalsIgnoreCase(nickname)) {
                return p; // Spieler existiert bereits
            }
        }
        Player newPlayer = new Player(nickname);
        players.add(newPlayer);
        return newPlayer;
    }

    public synchronized List<Player> getPlayers() {
        return List.copyOf(players);
    }

    public synchronized boolean isReadyToStart() {
        return players.size() >= 2;
    }

    public synchronized void clearLobby() {
        players.clear();
    }
}
