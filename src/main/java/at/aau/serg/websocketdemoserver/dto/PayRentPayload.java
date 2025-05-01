package at.aau.serg.websocketdemoserver.dto;

/**
 * Payload für das Bezahlen von Miete an einen anderen Spieler.
 */
public class PayRentPayload {

    private int playerId;
    private int ownerId;
    private int tilePos;
    private String tileName;
    private int rentAmount;

    public PayRentPayload() {
        // Standard-Konstruktor für Deserialisierung
    }

    public PayRentPayload(int playerId, int ownerId, int tilePos, String tileName, int rentAmount) {
        this.playerId = playerId;
        this.ownerId = ownerId;
        this.tilePos = tilePos;
        this.tileName = tileName;
        this.rentAmount = rentAmount;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
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

    public int getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(int rentAmount) {
        this.rentAmount = rentAmount;
    }
}