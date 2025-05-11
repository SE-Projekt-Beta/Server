package at.aau.serg.websocketdemoserver.dto;

public class BankCardPayload {
    private int playerId;
    private int amount;
    private int newCash;
    private String description;

    public BankCardPayload(int playerId, int amount, int newCash, String description) {
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

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setNewCash(int newCash) {
        this.newCash = newCash;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
