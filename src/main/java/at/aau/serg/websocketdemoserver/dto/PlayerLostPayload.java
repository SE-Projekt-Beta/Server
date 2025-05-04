package at.aau.serg.websocketdemoserver.dto;

public class PlayerLostPayload {
    private int playerId;
    private String nickname;
    private boolean gameOver;
    private int cash;
    private String reason;

    public PlayerLostPayload() {}

    public PlayerLostPayload(int playerId, String nickname) {
        this.playerId = playerId;
        this.nickname = nickname;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
