package at.aau.serg.websocketdemoserver.dto;

public class BankCardDrawnPayload {
    private int playerId;
    private int amount;
    private int newCash;
    private String description;

    public BankCardDrawnPayload(int playerId, int amount, int newCash, String description) {
        this.playerId = playerId;
        this.amount = amount;
        this.newCash = newCash;
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

    public String getDescription() {
        return description;
    }
}
