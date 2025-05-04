package at.aau.serg.websocketdemoserver.dto;

public class BuildPropertyPayload {
    private int playerId;

    public BuildPropertyPayload() {}

    public BuildPropertyPayload(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
