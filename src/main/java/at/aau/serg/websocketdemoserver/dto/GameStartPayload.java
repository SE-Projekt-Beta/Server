package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class GameStartPayload {

    private List<CurrentPlayerPayload> playerOrder;

    public GameStartPayload() {
        // leerer Standard-Konstruktor für JSON
    }

    public GameStartPayload(List<CurrentPlayerPayload> playerOrder) {
        this.playerOrder = playerOrder;
    }

    public List<CurrentPlayerPayload> getPlayerOrder() {
        return playerOrder;
    }

    public void setPlayerOrder(List<CurrentPlayerPayload> playerOrder) {
        this.playerOrder = playerOrder;
    }

}
