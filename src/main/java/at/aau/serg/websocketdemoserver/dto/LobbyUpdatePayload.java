package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class LobbyUpdatePayload {
    private List<String> players;

    public LobbyUpdatePayload() {}

    public LobbyUpdatePayload(List<String> players) {
        this.players = players;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
