package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class LobbyUpdatePayload {
    @Getter
    @Setter
    private int lobbyId;
    @Getter
    @Setter
    private List<PlayerDTO> players;

    public LobbyUpdatePayload(int lobbyId, List<PlayerDTO> players) {
        this.lobbyId = lobbyId;
        this.players = players;
    }
    

}