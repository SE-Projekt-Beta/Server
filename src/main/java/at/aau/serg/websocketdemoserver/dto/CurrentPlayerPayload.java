package at.aau.serg.websocketdemoserver.dto;

public class CurrentPlayerPayload {
    private int playerId;

    public CurrentPlayerPayload() {
    }

    public CurrentPlayerPayload(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}