package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Routes lobby‐related WebSocket messages.
 */
@Controller
public class LobbyWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    public LobbyWebSocketController(SimpMessagingTemplate messagingTemplate,
                                    LobbyService lobbyService) {
        this.messagingTemplate = messagingTemplate;
        this.lobbyService = lobbyService;
    }

    /**
     * Handles CREATE_LOBBY and LIST_LOBBIES.
     * Clients send to /app/lobby, subscribe to /topic/lobby.
     */
    @MessageMapping("/lobby")
    public void handleGeneral(@Payload LobbyMessage message) {
        System.out.println("Lobby (general): " + message.getType());
        List<LobbyMessage> responses = lobbyService.handle(message);
        responses.forEach(resp ->
                messagingTemplate.convertAndSend("/topic/lobby", resp)
        );
    }

    /**
     * Handles JOIN_LOBBY and START_GAME for a specific lobby.
     * Clients send to /app/lobby/{lobbyId}, subscribe to /topic/lobby/{lobbyId}.
     */
    @MessageMapping("/lobby/{lobbyId}")
    public void handleByLobby(@DestinationVariable int lobbyId,
                              @Payload LobbyMessage message) {
        System.out.println("Lobby " + lobbyId + ": " + message.getType());
        message.setLobbyId(lobbyId);
        List<LobbyMessage> responses = lobbyService.handle(message);
        for (var resp : responses) {
            if (resp.getType() == LobbyMessageType.LOBBY_LIST) {
                // still broadcast lobby list globally
                messagingTemplate.convertAndSend("/topic/lobby", resp);
            } else {
                // per‐lobby updates
                messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, resp);
            }
        }
    }
}