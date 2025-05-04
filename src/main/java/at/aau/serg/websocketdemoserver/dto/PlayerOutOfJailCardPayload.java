package at.aau.serg.websocketdemoserver.dto;

public class PlayerOutOfJailCardPayload {
    private int playerId;
    private String message;

    public PlayerOutOfJailCardPayload() {}

    public PlayerOutOfJailCardPayload(int playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
