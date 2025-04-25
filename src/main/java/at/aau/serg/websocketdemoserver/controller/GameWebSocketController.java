package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;


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
        System.out.println("Empfangen (DKT): " + message.getType());

        GameMessage result = gameHandler.handle(message);

        if (result != null) {
            messagingTemplate.convertAndSend("/topic/dkt", result);
        }

        for (GameMessage extra : gameHandler.getExtraMessages()) {
            messagingTemplate.convertAndSend("/topic/dkt", extra);
        }
    }
}