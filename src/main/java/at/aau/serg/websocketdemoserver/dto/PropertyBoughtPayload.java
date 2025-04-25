package at.aau.serg.websocketdemoserver.dto;

public class PropertyBoughtPayload {
    private String playerId;
    private int tilePos;
    private String tileName;

    public PropertyBoughtPayload() {
    }

    public PropertyBoughtPayload(String playerId, int tilePos, String tileName) {
        this.playerId = playerId;
        this.tilePos = tilePos;
        this.tileName = tileName;
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

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }
}
