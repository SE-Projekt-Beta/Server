package at.aau.serg.websocketdemoserver.dto;

public class AskBuyPropertyPayload {

    private int playerId;
    private int tilePos;
    private String tileName;
    private int price;

    // Standard-Konstruktor (wird für JSON-Deserialisierung benötigt)
    public AskBuyPropertyPayload() {
    }

    // Neuer Konstruktor für sauberen Aufbau im Server
    public AskBuyPropertyPayload(int playerId, int tilePos, String tileName, int price) {
        this.playerId = playerId;
        this.tilePos = tilePos;
        this.tileName = tileName;
        this.price = price;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
