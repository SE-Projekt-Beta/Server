package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Controller
public class GameWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameHandler gameHandler;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate, GameHandler gameHandler) {
        this.messagingTemplate = messagingTemplate;
        this.gameHandler = gameHandler;
    }

    @MessageMapping("/dkt")
    @SendTo("/topic/dkt")
    public GameMessage handleGameMessage(@Payload GameMessage message) {
        System.out.println("DKT empfangen: " + message.getType());

        GameMessage result = gameHandler.handle(message);

        for (GameMessage extra : gameHandler.getExtraMessages()) {
            System.out.println("→ Extra: " + extra.getType());
            messagingTemplate.convertAndSend("/topic/dkt", extra);
        }

        return result;
    }
}
