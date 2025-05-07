package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class StartGamePayload {
    private int lobbyId;

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }
}