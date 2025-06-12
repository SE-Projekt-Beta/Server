package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CashTaskPayload {
    private int playerId;
    private int amount;
    private int newCash;

    public CashTaskPayload(int playerId, int amount, int newCash) {
        this.playerId = playerId;
        this.amount = amount;
        this.newCash = newCash;
    }

}
