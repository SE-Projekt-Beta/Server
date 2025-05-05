package at.aau.serg.websocketdemoserver.dto;

public class GoToJailPayload {

    private int playerId;
    private int jailPosition;
    private int roundsInJail;
    private String reason;

    public GoToJailPayload() {}

    public GoToJailPayload(int playerId, int jailPosition, int roundsInJail, String reason) {
        this.playerId = playerId;
        this.jailPosition = jailPosition;
        this.roundsInJail = roundsInJail;
        this.reason = reason;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getJailPosition() {
        return jailPosition;
    }

    public void setJailPosition(int jailPosition) {
        this.jailPosition = jailPosition;
    }

    public int getRoundsInJail() {
        return roundsInJail;
    }

    public void setRoundsInJail(int roundsInJail) {
        this.roundsInJail = roundsInJail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
