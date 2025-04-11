package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class LobbyUpdatePayload {
    private List<PlayerInfo> players;

    public LobbyUpdatePayload() {}

    public LobbyUpdatePayload(List<PlayerInfo> players) {
        this.players = players;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInfo> players) {
        this.players = players;
    }
}
