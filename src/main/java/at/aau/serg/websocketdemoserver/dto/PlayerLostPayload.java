package at.aau.serg.websocketdemoserver.dto;

public class PlayerLostPayload {
    private final int playerId;
    private final String nickname;

    public PlayerLostPayload(int playerId, String nickname) {
        this.playerId = playerId;
        this.nickname = nickname;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getNickname() {
        return nickname;
    }
}

