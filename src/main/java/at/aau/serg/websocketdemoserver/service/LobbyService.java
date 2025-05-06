package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.lobby_request.*;
import org.springframework.stereotype.Service;

import java.util.*;

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
        handlerMap.put(LobbyMessageType.PLAYER_INIT, new InitPlayerRequest());
    }

    public List<Object> handle(LobbyMessage message) {
        List<Object> results = new ArrayList<>();

        if (message == null || message.getType() == null) {
            results.add(new LobbyMessage(LobbyMessageType.ERROR, "Ungültige Nachricht."));
            return results;
        }

        LobbyHandlerInterface handler = handlerMap.get(message.getType());
        if (handler == null) {
            results.add(new LobbyMessage(LobbyMessageType.ERROR, "Unbekannter Nachrichtentyp: " + message.getType()));
            return results;
        }

        LobbyMessage result = handler.execute(gameState, message.getPayload());
        results.add(result);

        if (message.getType() == LobbyMessageType.START_GAME) {
            // zusätzlich GameMessage (START_GAME mit GameStartedPayload) anfügen
            results.addAll(gameHandler.getExtraMessages());
        }

        return results;
    }
}
