package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateLobbyPayload {
    private String lobbyName;

}