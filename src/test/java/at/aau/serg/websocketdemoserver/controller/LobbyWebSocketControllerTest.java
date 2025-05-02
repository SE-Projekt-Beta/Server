package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    public LobbyWebSocketController(SimpMessagingTemplate messagingTemplate, LobbyService lobbyService) {
        this.messagingTemplate = messagingTemplate;
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/lobby")
    public void handleLobbyMessage(LobbyMessage message) {
        GameMessage response = lobbyService.handle(message);
        messagingTemplate.convertAndSend("/topic/lobby", response);
    }
}
