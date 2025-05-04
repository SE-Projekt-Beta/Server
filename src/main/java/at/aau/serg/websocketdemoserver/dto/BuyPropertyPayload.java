package at.aau.serg.websocketdemoserver.dto;

public class BuyPropertyPayload {

    private int playerId;
    private int tilePosition;
    private String tileName;
    private int price;

    public BuyPropertyPayload() {
    }

    public BuyPropertyPayload(int playerId, int tilePosition, String tileName, int price) {
        this.playerId = playerId;
        this.tilePosition = tilePosition;
        this.tileName = tileName;
        this.price = price;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTilePosition() {
        return tilePosition;
    }

    public void setTilePosition(int tilePosition) {
        this.tilePosition = tilePosition;
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
