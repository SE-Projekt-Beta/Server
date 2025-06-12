package at.aau.serg.websocketdemoserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class LobbyUpdatePayload {
    private int lobbyId;
    private List<PlayerDTO> players;

}