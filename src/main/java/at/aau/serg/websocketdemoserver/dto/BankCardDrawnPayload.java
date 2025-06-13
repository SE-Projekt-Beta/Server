package at.aau.serg.websocketdemoserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BankCardDrawnPayload {
    private int playerId;
    private int amount;
    private int newCash;
    private String description;
}
