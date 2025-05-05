package at.aau.serg.websocketdemoserver.dto;

public class DrawEventCardPayload {
    private int playerId;

    public DrawEventCardPayload() {}

    public DrawEventCardPayload(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
