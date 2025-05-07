package at.aau.serg.websocketdemoserver.dto;

public class PlayerMovePayload {
    private String playerId;
    private int pos;
    private int dice;
    private String tileName;
    private String tileType;

    public PlayerMovePayload() {
    }

    public PlayerMovePayload(String playerId, int pos, int dice, String tileName, String tileType) {
        this.playerId = playerId;
        this.pos = pos;
        this.dice = dice;
        this.tileName = tileName;
        this.tileType = tileType;
    }

    // Getter und Setter
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getDice() {
        return dice;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public String getTileType() {
        return tileType;
    }

    public void setTileType(String tileType) {
        this.tileType = tileType;
    }
}
