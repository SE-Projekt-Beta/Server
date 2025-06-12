package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayTaxPayload {
    private final int playerId;
    private final int amount;
    private final int newCash;
    private final String tileName;

    public PayTaxPayload(int playerId, int amount, int newCash, String tileName) {
        this.playerId = playerId;
        this.amount = amount;
        this.newCash = newCash;
        this.tileName = tileName;
    }

}
