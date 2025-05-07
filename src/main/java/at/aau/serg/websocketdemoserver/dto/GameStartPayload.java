package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class GameStartPayload {

    private List<PlayerDTO> playerOrder;

    public GameStartPayload() {
        // leerer Standard-Konstruktor für JSON
    }

    public GameStartPayload(List<PlayerDTO> playerOrder) {
        this.playerOrder = playerOrder;
    }

    public List<PlayerDTO> getPlayerOrder() {
        return playerOrder;
    }

    public void setPlayerOrder(List<PlayerDTO> playerOrder) {
        this.playerOrder = playerOrder;
    }

}
