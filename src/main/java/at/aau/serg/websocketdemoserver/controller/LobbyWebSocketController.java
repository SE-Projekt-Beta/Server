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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes lobby‐related WebSocket messages.
 */
@Controller
public class LobbyWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(LobbyWebSocketController.class);

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
        logger.info("Lobby (general): {}", message.getType());
        List<LobbyMessage> responses = lobbyService.handle(message);
        responses.forEach(resp ->
                messagingTemplate.convertAndSend("/topic/lobby", resp)
        );
    }
}