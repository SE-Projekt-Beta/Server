package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;

public interface LobbyHandlerInterface {

    LobbyMessageType getType();

    LobbyMessage execute(GameState gameState, Object parameter);
}

