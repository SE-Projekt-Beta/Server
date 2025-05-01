package at.aau.serg.websocketdemoserver.dto;

/**
 * Payload für die Anfrage, ein Grundstück zu kaufen.
 */
public class BuyPropertyPayload {

    private int playerId;
    private int tilePos;

    public BuyPropertyPayload() {
        // Standard-Konstruktor für Deserialisierung
    }

    public BuyPropertyPayload(int playerId, int tilePos) {
        this.playerId = playerId;
        this.tilePos = tilePos;
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
}