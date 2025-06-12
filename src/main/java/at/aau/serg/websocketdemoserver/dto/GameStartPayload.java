package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class GameStartPayload {

    @Getter
    @Setter
    private List<PlayerDTO> playerOrder;

    public GameStartPayload(List<PlayerDTO> playerOrder) {
        this.playerOrder = playerOrder;
    }

}