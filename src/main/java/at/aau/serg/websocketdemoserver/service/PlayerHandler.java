package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

import java.util.List;

// it saves a list of players and their ids

public class PlayerHandler {
    private List<Player> players = List.of();

    public List<Player> getPlayers() {
        return players;
    }

    // get a player by id
    public Player getPlayerById(int id) {
        return players.stream()
                .filter(player -> player.getId() == id)
                .findFirst()
                .orElse(null);
    }
    // get a player by name
    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(player -> player.getNickname().equals(name))
                .findFirst()
                .orElse(null);
    }
    // add a player to the list
    public void addPlayer(Player player) {
        players.add(player);
    }

    // remove a player from the list
    public void removePlayer(Player player) {
        players.remove(player);
    }
}


