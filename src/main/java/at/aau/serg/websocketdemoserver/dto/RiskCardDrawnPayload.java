package at.aau.serg.websocketdemoserver.dto;

public class RiskCardDrawnPayload {
    private int playerId;
    private int amount;
    private int newCash;
    private String title;
    private String description;

    public RiskCardDrawnPayload(int playerId, int amount, int newCash, String title, String description) {
        this.playerId = playerId;
        this.amount = amount;
        this.newCash = newCash;
        this.title = title;
        this.description = description;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getAmount() {
        return amount;
    }

    public int getNewCash() {
        return newCash;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
