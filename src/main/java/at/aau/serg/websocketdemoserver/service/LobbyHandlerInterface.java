package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;

public interface LobbyHandlerInterface {
    LobbyMessage execute(GameState gameState, Object parameter);
}

