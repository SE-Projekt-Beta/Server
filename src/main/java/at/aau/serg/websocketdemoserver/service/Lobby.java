package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.PlayerDTO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Lobby {

    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    private final List<PlayerDTO> players = new ArrayList<>();

    @Getter
    private final String lobbyName;

    public Lobby(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public synchronized PlayerDTO addPlayer(PlayerDTO player) {
        logger.info("Adding player {}", player);
        players.add(player);
        return player;
    }

    public synchronized List<PlayerDTO> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public synchronized boolean isReadyToStart() {
        logger.info("Lobby {} is ready to start: {}", lobbyName, players.size() >= 2);
        return players.size() >= 2; // At least 2 players required
    }

    public synchronized void clear() {
        players.clear();
    }

    public String getLobbyName() {
        return lobbyName;
    }
}