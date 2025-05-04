package at.aau.serg.websocketdemoserver.dto;

public class JoinLobbyPayload {
    private String nickname;

    public JoinLobbyPayload() {}

    public JoinLobbyPayload(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}