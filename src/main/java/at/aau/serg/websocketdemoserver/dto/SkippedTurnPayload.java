package at.aau.serg.websocketdemoserver.dto;

/**
 * Payload zur Mitteilung, dass ein Spieler übersprungen wird (z.B. wegen Gefängnis).
 */
public class SkippedTurnPayload {

    private int playerId;
    private int position;
    private String tileName;

    public SkippedTurnPayload() {
        // Standard-Konstruktor für Deserialisierung
    }

    public SkippedTurnPayload(int playerId, int position, String tileName) {
        this.playerId = playerId;
        this.position = position;
        this.tileName = tileName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }
}