package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
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
        List<Object> responses = lobbyService.handle(message);

        for (Object response : responses) {
            if (response instanceof LobbyMessage lobbyMsg) {
                messagingTemplate.convertAndSend("/topic/lobby", lobbyMsg);
            } else if (response instanceof GameMessage gameMsg) {
                messagingTemplate.convertAndSend("/topic/dkt", gameMsg);
            }
        }
    }
}
