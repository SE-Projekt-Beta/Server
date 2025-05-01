package at.aau.serg.websocketdemoserver.dto;


/**
 * Payload zur Mitteilung, welcher Spieler aktuell an der Reihe ist.
 */
public class CurrentPlayerPayload {

    private int playerId;

    public CurrentPlayerPayload() {
        // Standard-Konstruktor für Deserialisierung
    }

    public CurrentPlayerPayload(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
