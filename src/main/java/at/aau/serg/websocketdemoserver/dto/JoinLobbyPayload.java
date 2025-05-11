package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

public class JoinLobbyPayload {
    @Getter
    @Setter
    private int lobbyId;
    @Getter
    @Setter
    private Integer playerId;
}