package at.aau.serg.websocketdemoserver.dto;

public class PayRentPayload {
    private int fromPlayerId;
    private int toPlayerId;
    private int amount;

    public int getFromPlayerId() {
        return fromPlayerId;
    }

    public void setFromPlayerId(int fromPlayerId) {
        this.fromPlayerId = fromPlayerId;
    }

    public int getToPlayerId() {
        return toPlayerId;
    }

    public void setToPlayerId(int toPlayerId) {
        this.toPlayerId = toPlayerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}