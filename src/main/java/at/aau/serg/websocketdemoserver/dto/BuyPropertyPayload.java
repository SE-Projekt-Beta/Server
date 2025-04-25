package at.aau.serg.websocketdemoserver.dto;

public class BuyPropertyPayload {
    private String playerId;
    private int tilePos;

    public BuyPropertyPayload() {}

    public BuyPropertyPayload(String playerId, int tilePos) {
        this.playerId = playerId;
        this.tilePos = tilePos;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getTilePos() {
        return tilePos;
    }

    public void setTilePos(int tilePos) {
        this.tilePos = tilePos;
    }
}
