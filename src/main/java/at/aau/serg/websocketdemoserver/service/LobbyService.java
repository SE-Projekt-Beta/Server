package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
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
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        LobbyHandlerInterface::getType,
                        Function.identity()
                ));
    }

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

        // CREATE_LOBBY
        if (message.getType() == LobbyMessageType.CREATE_LOBBY) {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String name = (String) payload.get("lobbyName");
            GameState gamestate = lobbyManager.createLobby(name);
            responses.add(new LobbyMessage(
                    LobbyMessageType.LOBBY_CREATED,
                    gamestate.getLobbyId(),
                    gamestate.getLobbyName()
            ));
            return responses;
        }

        // LIST_LOBBIES
        if (message.getType() == LobbyMessageType.LIST_LOBBIES) {
            List<Map<String, Object>> lobbyList = lobbyManager.listLobbies().stream()
                    .map(lobby -> {
                        Map<String, Object> lobbyInfo = new HashMap<>();
                        lobbyInfo.put("lobbyId", lobby.getLobbyId());
                        lobbyInfo.put("lobbyName", lobby.getLobbyName());
                        lobbyInfo.put("playerCount", lobby.getPlayers().size());
                        return lobbyInfo;
                    }).collect(Collectors.toList());
            responses.add(new LobbyMessage(
                    LobbyMessageType.LOBBY_LIST,
                    lobbyList
            ));
            return responses;
        }

        // everything else: route by lobbyId
        String lobbyId = message.getLobbyId();
        GameState gameState = lobbyManager.getLobby(lobbyId);
        if (gameState == null) {
            responses.add(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    lobbyId,
                    "Lobby nicht gefunden: " + lobbyId
            ));
            return responses;
        }

        LobbyHandlerInterface handler = handlerMap.get(message.getType());
        if (handler == null) {
            responses.add(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    lobbyId,
                    "Unhandled message type: " + message.getType()
            ));
            return responses;
        }

        // ← pass lobbyId here
        LobbyMessage result = handler.execute(
                gameState,
                message.getPayload()
        );
        result.setLobbyId(lobbyId);
        responses.add(result);

        if (message.getType() == LobbyMessageType.START_GAME) {
            for (GameMessage gm : gameState.getGameHandler().getExtraMessages()) {
                responses.add(gm);
            }
        }

        return responses;
    }
}
