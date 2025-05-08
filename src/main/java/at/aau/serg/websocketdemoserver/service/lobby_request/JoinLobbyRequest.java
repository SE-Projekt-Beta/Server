package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.service.Lobby;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import at.aau.serg.websocketdemoserver.service.LobbyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class JoinLobbyRequest implements LobbyRequest {

    private final LobbyManager lobbyManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JoinLobbyRequest(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public List<LobbyMessage> handle(LobbyMessage message) {
        try {
            JoinLobbyPayload payload = objectMapper.convertValue(message.getPayload(), JoinLobbyPayload.class);
            int lobbyId = payload.getLobbyId();
            Lobby lobby = lobbyManager.getLobby(lobbyId);

            if (lobby == null) {
                return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Lobby not found."));
            }

            lobby.addPlayer(payload.getUsername());

            List<Map<String, Object>> lobbyList = lobbyManager.getLobbyIds().stream()
                    .map(id -> {
                        Lobby l = lobbyManager.getLobby(id);
                        Map<String, Object> map = new HashMap<>();
                        map.put("lobbyId", id);
                        map.put("lobbyName", l != null ? l.getLobbyName() : "Unknown");
                        map.put("playerCount", l != null ? l.getPlayers().size() : 0);
                        return map;
                    })
                    .toList();

            return List.of(
                    new LobbyMessage(lobbyId, LobbyMessageType.LOBBY_UPDATE,
                            new LobbyUpdatePayload(lobbyId, lobby.getPlayers())),
                    new LobbyMessage(LobbyMessageType.LOBBY_LIST, lobbyList)
            );
        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Error joining lobby: " + e.getMessage()));
        }
    }
}
