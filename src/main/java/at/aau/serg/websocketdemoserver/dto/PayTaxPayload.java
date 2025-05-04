package at.aau.serg.websocketdemoserver.dto;

public class PayTaxPayload {
    private int playerId;
    private String tileName;
    private int amount;
    private int oldCash;
    private int newCash;

    public PayTaxPayload() {}

    public PayTaxPayload(int playerId, String tileName, int amount, int oldCash, int newCash) {
        this.playerId = playerId;
        this.tileName = tileName;
        this.amount = amount;
        this.oldCash = oldCash;
        this.newCash = newCash;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getOldCash() {
        return oldCash;
    }

    public void setOldCash(int oldCash) {
        this.oldCash = oldCash;
    }

    public int getNewCash() {
        return newCash;
    }

    public void setNewCash(int newCash) {
        this.newCash = newCash;
    }
}
