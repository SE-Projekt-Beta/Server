package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.lobby_request.*;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LobbyService {

    private final GameState gameState;
    private final GameHandler gameHandler;
    private final Map<LobbyMessageType, LobbyHandlerInterface> handlerMap = new HashMap<>();

    public LobbyService(GameState gameState, GameHandler gameHandler) {
        this.gameState = gameState;
        this.gameHandler = gameHandler;
        registerHandlers();
    }

    private void registerHandlers() {
        handlerMap.put(LobbyMessageType.JOIN_LOBBY, new JoinLobbyRequest());
        handlerMap.put(LobbyMessageType.LEAVE_LOBBY, new LeaveLobbyRequest());
        handlerMap.put(LobbyMessageType.START_GAME, new StartGameRequest(gameHandler));
        handlerMap.put(LobbyMessageType.LOBBY_UPDATE, new LobbyUpdateRequest());
        handlerMap.put(LobbyMessageType.PLAYER_INIT, new InitPlayerRequest());
    }

    public List<LobbyMessage> handle(LobbyMessage message) {
        LobbyHandlerInterface handler = handlerMap.get(message.getType());
        if (handler == null) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Unbekannter Nachrichtentyp"));
        }

        LobbyMessage result = handler.execute(gameState, message.getPayload());
        return List.of(result);
    }
}
