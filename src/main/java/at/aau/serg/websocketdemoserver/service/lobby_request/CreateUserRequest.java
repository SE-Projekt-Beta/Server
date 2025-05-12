package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import at.aau.serg.websocketdemoserver.service.LobbyRequest;
import at.aau.serg.websocketdemoserver.service.UserManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class CreateUserRequest implements LobbyRequest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserManager userManager;

    public CreateUserRequest(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public List<LobbyMessage> handle(LobbyMessage message) {
        try {
//            fun sendCreateUser(username: String) {
//                val payload = JsonObject().apply {
//                    addProperty("username", username);
//                }
//                val msg = LobbyMessage(null, LobbyMessageType.CREATE_USER, payload)
//                scope.launch { session.sendText("/app/lobby", Gson().toJson(msg)) }
//            }
//            client code ^



            CreateUserPayload payload = objectMapper.convertValue(message.getPayload(), CreateUserPayload.class);
            String username = payload.getUsername();
            PlayerDTO player = userManager.createUser(username);

            Integer playerId = player.getId();
            String playerIdString = playerId.toString();
            String userName = player.getNickname();
            Map<String, String> userPayload = Map.of("playerId", playerIdString, "username", userName);
            return List.of(new LobbyMessage(0, LobbyMessageType.USER_CREATED, userPayload));
        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Error creating user: " + e.getMessage()));
        }
    }
}

