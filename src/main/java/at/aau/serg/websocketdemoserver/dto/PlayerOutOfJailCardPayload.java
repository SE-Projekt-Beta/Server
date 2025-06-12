package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

public class PlayerOutOfJailCardPayload {
    @Getter
    @Setter
    private int playerId;
    @Getter
    @Setter
    private String message;

    public PlayerOutOfJailCardPayload(int playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }

}
