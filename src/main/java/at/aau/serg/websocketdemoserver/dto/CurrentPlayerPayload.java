package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CurrentPlayerPayload {
    private int playerId;

    public CurrentPlayerPayload(int playerId) {
        this.playerId = playerId;
    }

}