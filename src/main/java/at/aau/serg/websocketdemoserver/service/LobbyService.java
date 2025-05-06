package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.Lobby;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LobbyService {

    private final LobbyManager lobbyManager;
    private final Map<LobbyMessageType, LobbyHandlerInterface> handlerMap;

    public LobbyService(LobbyManager lobbyManager,
                        List<LobbyHandlerInterface> handlers) {
        this.lobbyManager = lobbyManager;
        // build a map from message‐type to the handler that declared it
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        LobbyHandlerInterface::getType,
                        Function.identity()
                ));
    }

    /**
     * Entry point for all lobby‐scoped messages.
     * Returns a mix of LobbyMessage (for lobby‐protocol responses)
     * and GameMessage (for the START_GAME extra payloads).
     */
    public List<Object> handle(LobbyMessage message) {
        List<Object> responses = new ArrayList<>();

        // sanity check
        if (message == null || message.getType() == null) {
            responses.add(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    null,
                    "Ungültige Nachricht."
            ));
            return responses;
        }

        // CREATE_LOBBY is special: no existing lobbyId needed
        if (message.getType() == LobbyMessageType.CREATE_LOBBY) {
            String name = (String) message.getPayload();
            Lobby lobby = lobbyManager.createLobby(name);
            responses.add(new LobbyMessage(
                    LobbyMessageType.LOBBY_CREATED,
                    lobby.getId(),
                    lobby.getName()
            ));
            return responses;
        }

        // for everything else we need a valid lobbyId
        String lobbyId = message.getLobbyId();
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            responses.add(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    lobbyId,
                    "Lobby nicht gefunden: " + lobbyId
            ));
            return responses;
        }

        // delegate to the right handler
        LobbyHandlerInterface handler = handlerMap.get(message.getType());
        if (handler == null) {
            responses.add(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    lobbyId,
                    "Unhandled message type: " + message.getType()
            ));
            return responses;
        }

        // execute against *this* lobby's GameState
        LobbyMessage result = handler.execute(
                lobby.getGameState(),
                message.getPayload()
        );
        // stamp the response with our lobbyId
        result.setLobbyId(lobbyId);
        responses.add(result);

        // if we just started the game, also pull out the queued GameMessages
        if (message.getType() == LobbyMessageType.START_GAME) {
            for (GameMessage gm : lobby.getGameHandler().getExtraMessages()) {
                responses.add(gm);
            }
        }

        return responses;
    }
}
