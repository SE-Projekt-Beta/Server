package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.PlayerDTO;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UserManager {
    // array list ofr players
    private final List<PlayerDTO> players = new ArrayList<>();
    private int playerIdCounter = 1;

    public synchronized PlayerDTO createUser(String nickname) {
        PlayerDTO newPlayer = new PlayerDTO(playerIdCounter, nickname);
        playerIdCounter++;
        return newPlayer;
    }

    public synchronized List<PlayerDTO> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}