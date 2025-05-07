package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;

import java.util.List;

public interface GameRequest {
    GameMessage handle(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages);
}
