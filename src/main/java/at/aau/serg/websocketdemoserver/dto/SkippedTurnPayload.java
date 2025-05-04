package at.aau.serg.websocketdemoserver.dto;

public class SkippedTurnPayload {
    private int playerId;
    private String reason;
    private int tilePos;
    private String tileName;
    private int suspension;

    public SkippedTurnPayload() {}

    public SkippedTurnPayload(int playerId, String reason) {
        this.playerId = playerId;
        this.reason = reason;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getTilePos() {
        return tilePos;
    }

    public void setTilePos(int tilePos) {
        this.tilePos = tilePos;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public int getSuspension() {
        return suspension;
    }

    public void setSuspension(int suspension) {
        this.suspension = suspension;
    }
}
