package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;

@Getter
public class WentToJailPayload {
    private int playerId;

    public WentToJailPayload(int playerId) {
        this.playerId = playerId;
    }

}
