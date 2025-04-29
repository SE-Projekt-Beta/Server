package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LobbyService {

    private final LobbyManager lobbyManager = new LobbyManager();
    private final GameHandler gameHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LobbyService(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public List<LobbyMessage> handle(LobbyMessage message) {
        if (message == null || message.getType() == null) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Invalid or missing message."));
        }

        return switch (message.getType()) {
            case CREATE_LOBBY -> handleCreateLobby(message.getPayload());
            case LIST_LOBBIES -> handleListLobbies();
            case JOIN_LOBBY -> handleJoinLobby(message.getPayload());
            case START_GAME -> handleStartGame(message.getPayload());
            default -> List.of(new LobbyMessage(LobbyMessageType.ERROR, "Unknown message type: " + message.getType()));
        };
    }

    private List<LobbyMessage> handleCreateLobby(Object payload) {
        try {
            // Convert payload to CreateLobbyPayload DTO
            CreateLobbyPayload createPayload = objectMapper.convertValue(payload, CreateLobbyPayload.class);
            String lobbyName = createPayload.getLobbyName();

            // Create a new lobby
            int lobbyId = lobbyManager.createLobby(lobbyName);

            // Return a response with the created lobby ID
            return List.of(new LobbyMessage(LobbyMessageType.LOBBY_CREATED, lobbyId));
        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Error creating lobby: " + e.getMessage()));
        }
    }

//    private List<LobbyMessage> handleListLobbies() {
//        List<Integer> lobbyIds = lobbyManager.getLobbyIds();
//        return List.of(new LobbyMessage(LobbyMessageType.LOBBY_LIST, lobbyIds));
//    }

    private List<LobbyMessage> handleListLobbies() {
        List<Map<String, Object>> lobbyDetails = lobbyManager.getLobbyIds().stream()
                .map(lobbyId -> {
                    Lobby lobby = lobbyManager.getLobby(lobbyId);
                    Map<String, Object> details = new HashMap<>();
                    details.put("lobbyId", lobbyId);
                    details.put("lobbyName", lobby != null ? lobby.getLobbyName() : "Unknown");
                    details.put("playerCount", lobby != null ? lobby.getPlayers().size() : 0);
                    return details;
                })
                .toList();

        return List.of(new LobbyMessage(LobbyMessageType.LOBBY_LIST, lobbyDetails));
    }

    private List<LobbyMessage> handleJoinLobby(Object payload) {
        try {
            JoinLobbyPayload joinPayload = objectMapper.convertValue(payload, JoinLobbyPayload.class);
            int lobbyId = joinPayload.getLobbyId();
            Lobby lobby = lobbyManager.getLobby(lobbyId);

            if (lobby == null) {
                return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Lobby not found."));
            }

            PlayerDTO playerDTO = lobby.addPlayer(joinPayload.getUsername());
            return List.of(new LobbyMessage(
                    LobbyMessageType.LOBBY_UPDATE,
                    new LobbyUpdatePayload(lobby.getPlayers())
            ));
        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Error joining lobby: " + e.getMessage()));
        }
    }

    private List<LobbyMessage> handleStartGame(Object payload) {
        try {
            StartGamePayload startPayload = objectMapper.convertValue(payload, StartGamePayload.class);
            int lobbyId = startPayload.getLobbyId();
            Lobby lobby = lobbyManager.getLobby(lobbyId);

            if (lobby == null || !lobby.isReadyToStart()) {
                return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Lobby not ready to start."));
            }

            List<PlayerDTO> players = lobby.getPlayers();
            gameHandler.initGame(players);
            lobbyManager.removeLobby(lobbyId);

            return List.of(new LobbyMessage(
                    LobbyMessageType.START_GAME,
                    new GameStartPayload(players)
            ));
        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Error starting game: " + e.getMessage()));
        }
    }
}