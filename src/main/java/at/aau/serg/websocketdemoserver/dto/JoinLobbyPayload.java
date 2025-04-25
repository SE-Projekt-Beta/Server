package at.aau.serg.websocketdemoserver.dto;

public class JoinLobbyPayload {
    private String username;

    public JoinLobbyPayload() {}

    public JoinLobbyPayload(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
