package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;

import java.util.List;

public interface LobbyRequest {
    List<LobbyMessage> handle(LobbyMessage message);
}
