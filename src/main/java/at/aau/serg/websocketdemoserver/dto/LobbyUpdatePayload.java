package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class LobbyUpdatePayload {
    private List<PlayerDTO> players;

    public LobbyUpdatePayload() {}

    public LobbyUpdatePayload(List<PlayerDTO> players) {
        this.players = players;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }
}