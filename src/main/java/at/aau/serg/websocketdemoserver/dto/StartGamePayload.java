package at.aau.serg.websocketdemoserver.dto;

public class StartGamePayload {
    private int lobbyId;

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }
}