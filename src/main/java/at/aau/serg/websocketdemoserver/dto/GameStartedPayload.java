package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class GameStartedPayload {

    private List<PlayerLobbyEntry> playerOrder;

    public GameStartedPayload() {}

    public GameStartedPayload(List<PlayerLobbyEntry> playerOrder) {
        this.playerOrder = playerOrder;
    }

    public List<PlayerLobbyEntry> getPlayerOrder() {
        return playerOrder;
    }

    public void setPlayerOrder(List<PlayerLobbyEntry> playerOrder) {
        this.playerOrder = playerOrder;
    }
}
