package at.aau.serg.websocketdemoserver.dto;

public class WentToJailPayload {
    private int playerId;

    public WentToJailPayload(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
