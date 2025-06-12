package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerOutOfJailCardPayload {
    private int playerId;
    private String message;

    public PlayerOutOfJailCardPayload(int playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }

}
