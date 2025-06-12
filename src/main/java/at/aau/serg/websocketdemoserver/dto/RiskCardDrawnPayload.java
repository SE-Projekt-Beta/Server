package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;

@Getter
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

}
