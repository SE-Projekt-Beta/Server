package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.GameRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestGameStarted implements GameRequest {

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        Map<String, Object> info = new HashMap<>();
        info.put("currentRound", gameState.getCurrentRound());
        info.put("playerCount", gameState.getAllPlayers().size());
        info.put("currentPlayerId", gameState.getCurrentPlayerId());

        return new GameMessage(lobbyId, MessageType.GAME_STARTED, info);
    }
}
