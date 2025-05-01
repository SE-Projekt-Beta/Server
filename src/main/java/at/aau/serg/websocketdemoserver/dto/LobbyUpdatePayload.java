package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class LobbyUpdatePayload {
    private List<CurrentPlayerPayload> players;

    public LobbyUpdatePayload() {}

    public LobbyUpdatePayload(List<CurrentPlayerPayload> players) {
        this.players = players;
    }

    public List<CurrentPlayerPayload> getPlayers() {
        return players;
    }

    public void setPlayers(List<CurrentPlayerPayload> players) {
        this.players = players;
    }
}