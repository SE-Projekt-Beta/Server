package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.Lobby;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import at.aau.serg.websocketdemoserver.service.LobbyRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListLobbiesRequest implements LobbyRequest {

    private final LobbyManager lobbyManager;

    public ListLobbiesRequest(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public List<LobbyMessage> handle(LobbyMessage message) {
        List<Map<String, Object>> lobbyList = lobbyManager.getLobbyIds().stream()
                .map(id -> {
                    Lobby lobby = lobbyManager.getLobby(id);
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("lobbyId", id);
                    entry.put("lobbyName", lobby != null ? lobby.getLobbyName() : "Unknown");
                    entry.put("playerCount", lobby != null ? lobby.getPlayers().size() : 0);
                    return entry;
                }).toList();

        return List.of(new LobbyMessage(LobbyMessageType.LOBBY_LIST, lobbyList));
    }
}
