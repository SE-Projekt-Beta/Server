package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.CreateLobbyPayload;
import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import at.aau.serg.websocketdemoserver.service.LobbyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CreateLobbyRequest implements LobbyRequest {

    private final LobbyManager lobbyManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CreateLobbyRequest(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public List<LobbyMessage> handle(LobbyMessage message) {
        try {
            CreateLobbyPayload payload = objectMapper.convertValue(message.getPayload(), CreateLobbyPayload.class);
            int lobbyId = lobbyManager.createLobby(payload.getLobbyName());
            return List.of(new LobbyMessage(lobbyId, LobbyMessageType.LOBBY_CREATED, lobbyId));
        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Error creating lobby: " + e.getMessage()));
        }
    }
}
