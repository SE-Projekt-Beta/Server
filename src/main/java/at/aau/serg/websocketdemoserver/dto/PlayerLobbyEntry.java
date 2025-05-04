package at.aau.serg.websocketdemoserver.dto;

public class PlayerLobbyEntry {
    private int playerId;
    private String nickname;

    public PlayerLobbyEntry() {
    }

    public PlayerLobbyEntry(int playerId, String nickname) {
        this.playerId = playerId;
        this.nickname = nickname;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
