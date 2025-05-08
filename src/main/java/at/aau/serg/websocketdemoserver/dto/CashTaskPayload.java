package at.aau.serg.websocketdemoserver.dto;

public class CashTaskPayload {
    private int playerId;
    private int amount;
    private int newCash;

    public CashTaskPayload() {
    }

    public CashTaskPayload(int playerId, int amount, int newCash) {
        this.playerId = playerId;
        this.amount = amount;
        this.newCash = newCash;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getNewCash() {
        return newCash;
    }

    public void setNewCash(int newCash) {
        this.newCash = newCash;
    }
}
