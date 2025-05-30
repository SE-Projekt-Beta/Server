package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.service.GameManager;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Routes game‐related WebSocket messages to the correct GameHandler.
 */
@Controller
public class GameWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(GameWebSocketController.class);

    private final SimpMessagingTemplate messagingTemplate;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   LobbyService lobbyService) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Clients send to /app/dkt/{lobbyId}, subscribe to /topic/dkt/{lobbyId}.
     */
    @MessageMapping("/dkt/{lobbyId}")
    public void handleGameMessage(@DestinationVariable int lobbyId,
                                  @Payload GameMessage message) {
        if (lobbyId < 0) {
            System.err.println("Ungültige Lobby-ID: " + lobbyId);
            return;
        }
        logger.info("Game {}: {}", lobbyId, message.getType());
        message.setLobbyId(lobbyId);

        var handler = GameManager.getInstance().getHandler(lobbyId);
        if (handler == null) {
            System.err.println("Kein GameHandler für ID: " + lobbyId);
            return;
        }
        GameMessage result = handler.handle(message);
        logger.info("Sending result: {} to {}", message.getType(), lobbyId);
        if (result != null) {
            messagingTemplate.convertAndSend("/topic/dkt/" + lobbyId, result);
        }
        handler.getExtraMessages().forEach(extra ->
                messagingTemplate.convertAndSend("/topic/dkt/" + lobbyId, extra)
        );
    }

}