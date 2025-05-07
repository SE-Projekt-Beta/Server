package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class LobbyUpdatePayload {
    private int lobbyId;
    private List<PlayerDTO> players;

    public LobbyUpdatePayload() {}

    public LobbyUpdatePayload(int lobbyId, List<PlayerDTO> players) {
        this.lobbyId = lobbyId;
        this.players = players;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }
}