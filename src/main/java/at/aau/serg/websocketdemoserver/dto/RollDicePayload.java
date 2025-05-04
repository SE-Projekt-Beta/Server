package at.aau.serg.websocketdemoserver.dto;

public class RollDicePayload {
    private int playerId;

    public RollDicePayload() {}

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
