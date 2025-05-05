package at.aau.serg.websocketdemoserver.dto;

public class StartMoneyPayload {
    private int playerId;
    private int bonusAmount;
    private int newCash;

    public StartMoneyPayload() {}

    public StartMoneyPayload(int playerId, int bonusAmount, int newCash) {
        this.playerId = playerId;
        this.bonusAmount = bonusAmount;
        this.newCash = newCash;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(int bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public int getNewCash() {
        return newCash;
    }

    public void setNewCash(int newCash) {
        this.newCash = newCash;
    }
}
