package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Routes game‐related WebSocket messages to the correct GameHandler.
 */
@Controller
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   LobbyService lobbyService) {
        this.messagingTemplate = messagingTemplate;
        this.lobbyService = lobbyService;
    }

    /**
     * Clients send to /app/dkt/{lobbyId}, subscribe to /topic/dkt/{lobbyId}.
     */
    @MessageMapping("/dkt/{lobbyId}")
    public void handleGameMessage(@DestinationVariable int lobbyId,
                                  @Payload GameMessage message) {
        System.out.println("Game " + lobbyId + ": " + message.getType());
        message.setLobbyId(lobbyId);
        var handler = lobbyService.getGameHandler(lobbyId);
        GameMessage result = handler.handle(message);
        if (result != null) {
            messagingTemplate.convertAndSend("/topic/dkt/" + lobbyId, result);
        }
        handler.getExtraMessages().forEach(extra ->
                messagingTemplate.convertAndSend("/topic/dkt/" + lobbyId, extra)
        );
    }
}
