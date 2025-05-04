package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameHandler gameHandler;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate, GameHandler gameHandler) {
        this.messagingTemplate = messagingTemplate;
        this.gameHandler = gameHandler;
    }

    @MessageMapping("/dkt")
    public void handleGameMessage(@Payload GameMessage message) {
        GameMessage response = gameHandler.handle(message);
        messagingTemplate.convertAndSend("/topic/dkt", response);

        for (GameMessage extra : gameHandler.getExtraMessages()) {
            messagingTemplate.convertAndSend("/topic/dkt", extra);
        }
    }
}
