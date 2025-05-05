package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;

public interface GameHandlerInterface {
    GameMessage execute(GameState gameState, GameMessage message);
}
