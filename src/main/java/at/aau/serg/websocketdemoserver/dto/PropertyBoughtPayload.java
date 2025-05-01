package at.aau.serg.websocketdemoserver.dto;

/**
 * Payload für die Bestätigung, dass ein Spieler ein Grundstück erfolgreich gekauft hat.
 */
public class PropertyBoughtPayload {

    private int playerId;
    private int tilePos;
    private String tileName;

    public PropertyBoughtPayload() {
        // Standard-Konstruktor für Deserialisierung
    }

    public PropertyBoughtPayload(int playerId, int tilePos, String tileName) {
        this.playerId = playerId;
        this.tilePos = tilePos;
        this.tileName = tileName;
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
}