package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service handling lobby operations, now with one GameHandler per lobby.
 */
@Service
public class LobbyService {

    private final LobbyManager lobbyManager = new LobbyManager();
    // Map storing a GameHandler instance for each lobbyId
    private final Map<Integer, GameHandler> gameHandlers = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get (or create) the GameHandler for a given lobby.
     */
    public GameHandler getGameHandler(int lobbyId) {
        return gameHandlers.computeIfAbsent(lobbyId, id -> new GameHandler());
    }

    /**
     * Entry point for all LobbyMessage types.
     */
    public List<LobbyMessage> handle(LobbyMessage message) {
        if (message == null || message.getType() == null) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Invalid or missing message."));
        }

        return switch (message.getType()) {
            case CREATE_LOBBY -> handleCreateLobby(message.getPayload());
            case LIST_LOBBIES -> handleListLobbies();
            case JOIN_LOBBY   -> handleJoinLobby(message.getPayload());
            case START_GAME   -> handleStartGame(message.getPayload());
            default -> List.of(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    "Unknown message type: " + message.getType()
            ));
        };
    }

    private List<LobbyMessage> handleCreateLobby(Object payload) {
        try {
            CreateLobbyPayload p = objectMapper.convertValue(payload, CreateLobbyPayload.class);
            int lobbyId = lobbyManager.createLobby(p.getLobbyName());
            // Notify the creator on /topic/lobby/{lobbyId}
            return List.of(new LobbyMessage(
                    lobbyId,
                    LobbyMessageType.LOBBY_CREATED,
                    lobbyId
            ));
        } catch (Exception e) {
            return List.of(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    "Error creating lobby: " + e.getMessage()
            ));
        }
    }

    private List<LobbyMessage> handleListLobbies() {
        List<Map<String, Object>> details = getLobbyDetails();
        // Broadcast to everyone on /topic/lobby
        return List.of(new LobbyMessage(
                LobbyMessageType.LOBBY_LIST,
                details
        ));
    }

    private List<LobbyMessage> handleJoinLobby(Object payload) {
        try {
            JoinLobbyPayload p = objectMapper.convertValue(payload, JoinLobbyPayload.class);
            int lobbyId = p.getLobbyId();
            Lobby lobby = lobbyManager.getLobby(lobbyId);
            if (lobby == null) {
                return List.of(new LobbyMessage(
                        LobbyMessageType.ERROR,
                        "Lobby not found."
                ));
            }
            lobby.addPlayer(p.getUsername());
            List<Map<String, Object>> details = getLobbyDetails();
            return List.of(
                    // Per-lobby update
                    new LobbyMessage(
                            lobbyId,
                            LobbyMessageType.LOBBY_UPDATE,
                            new LobbyUpdatePayload(lobbyId, lobby.getPlayers())
                    ),
                    // Global lobby list update
                    new LobbyMessage(
                            LobbyMessageType.LOBBY_LIST,
                            details
                    )
            );
        } catch (Exception e) {
            return List.of(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    "Error joining lobby: " + e.getMessage()
            ));
        }
    }

    private List<LobbyMessage> handleStartGame(Object payload) {
        try {
            StartGamePayload p = objectMapper.convertValue(payload, StartGamePayload.class);
            int lobbyId = p.getLobbyId();
            Lobby lobby = lobbyManager.getLobby(lobbyId);
            if (lobby == null || !lobby.isReadyToStart()) {
                return List.of(new LobbyMessage(
                        LobbyMessageType.ERROR,
                        "Lobby not ready to start."
                ));
            }
            GameHandler handler = getGameHandler(lobbyId);
            handler.initGame(lobby.getPlayers());
            lobbyManager.removeLobby(lobbyId);
            // Tell just this lobby to start the game
            return List.of(new LobbyMessage(
                    lobbyId,
                    LobbyMessageType.START_GAME,
                    new GameStartPayload(lobby.getPlayers())
            ));
        } catch (Exception e) {
            return List.of(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    "Error starting game: " + e.getMessage()
            ));
        }
    }

    private List<Map<String, Object>> getLobbyDetails() {
        return lobbyManager.getLobbyIds().stream()
                .map(lobbyId -> {
                    Lobby lobby = lobbyManager.getLobby(lobbyId);
                    Map<String, Object> info = new HashMap<>();
                    info.put("lobbyId", lobbyId);
                    info.put("lobbyName", lobby != null ? lobby.getLobbyName() : "Unknown");
                    info.put("playerCount", lobby != null ? lobby.getPlayers().size() : 0);
                    return info;
                })
                .toList();
    }
}