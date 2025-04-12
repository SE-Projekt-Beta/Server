package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Controller
public class GameWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameHandler gameHandler;
    private final LobbyService lobbyService;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate, GameHandler gameHandler, LobbyService lobbyService) {
        this.messagingTemplate = messagingTemplate;
        this.gameHandler = gameHandler;
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/dkt")
    public void handleGameMessage(@Payload GameMessage message) {
        System.out.println("Empfangen: " + message.getType());

        GameMessage result = routeMessage(message);

        if (result != null) {
            messagingTemplate.convertAndSend("/topic/dkt", result);
        }

        // Falls Extra-Nachrichten existieren (z.B. CAN_BUY_PROPERTY nach MOVED)
        for (GameMessage extra : gameHandler.getExtraMessages()) {
            System.out.println("→ Extra: " + extra.getType());
            messagingTemplate.convertAndSend("/topic/dkt", extra);
        }
    }

    private GameMessage routeMessage(GameMessage msg) {
        switch (msg.getType()) {
            case JOIN_LOBBY:
                return lobbyService.handleJoinLobby(msg.getPayload());
            case START_GAME:
                return lobbyService.handleStartGame();
            case ROLL_DICE:
            case BUY_PROPERTY:
                return gameHandler.handle(msg);
            default:
                System.out.println("Unbekannter Typ: " + msg.getType());
                return null;
        }
    }
}
