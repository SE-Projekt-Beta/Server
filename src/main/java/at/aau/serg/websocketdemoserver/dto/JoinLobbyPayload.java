package at.aau.serg.websocketdemoserver.dto;

public class JoinLobbyPayload {
    private int lobbyId;
    private String username;

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}