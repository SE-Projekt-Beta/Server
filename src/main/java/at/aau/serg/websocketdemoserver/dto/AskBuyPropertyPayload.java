package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

public class AskBuyPropertyPayload {

    @Getter
    @Setter
    private int playerId;
    @Getter
    @Setter
    private int tilePos;
    @Getter
    @Setter
    private String tileName;
    @Getter
    @Setter
    private int price;

    // Neuer Konstruktor für sauberen Aufbau im Server
    public AskBuyPropertyPayload(int playerId, int tilePos, String tileName, int price) {
        this.playerId = playerId;
        this.tilePos = tilePos;
        this.tileName = tileName;
        this.price = price;
    }
}
