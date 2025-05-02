package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class LobbyWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    public LobbyWebSocketController(SimpMessagingTemplate messagingTemplate, LobbyService lobbyService) {
        this.messagingTemplate = messagingTemplate;
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/lobby")
    public void handleLobbyMessage(@Payload LobbyMessage message) {
        List<LobbyMessage> responses = lobbyService.handle(message);
        for (LobbyMessage response : responses) {
            messagingTemplate.convertAndSend("/topic/lobby", response);
        }
    }
}
