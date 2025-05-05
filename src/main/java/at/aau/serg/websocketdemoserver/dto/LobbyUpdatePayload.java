package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class LobbyUpdatePayload {
    private List<PlayerLobbyEntry> players;

    public LobbyUpdatePayload() {}

    public LobbyUpdatePayload(List<PlayerLobbyEntry> players) {
        this.players = players;
    }

    public List<PlayerLobbyEntry> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerLobbyEntry> players) {
        this.players = players;
    }
}