package at.aau.serg.websocketdemoserver.dto;

public class BuildPropertyPayload {
    private int playerId;
    private Integer tilePos; // optional, falls Bau auf anderem Feld erlaubt werden soll

    public BuildPropertyPayload() {}

    public BuildPropertyPayload(int playerId) {
        this.playerId = playerId;
    }

    public BuildPropertyPayload(int playerId, Integer tilePos) {
        this.playerId = playerId;
        this.tilePos = tilePos;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Integer getTilePos() {
        return tilePos;
    }

    public void setTilePos(Integer tilePos) {
        this.tilePos = tilePos;
    }
}