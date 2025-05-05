package at.aau.serg.websocketdemoserver.dto;

public class MovePlayerPayload {
    private int playerId;
    private int pos;            // neue Position nach Bewegung
    private int dice;           // gewürfelte Zahl
    private String tileName;    // Name des Feldes
    private String tileType;    // Typ des Feldes (z. B. StreetTile, RiskTile)

    public MovePlayerPayload() {}

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
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
