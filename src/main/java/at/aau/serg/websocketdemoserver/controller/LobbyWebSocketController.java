package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class LobbyWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    public LobbyWebSocketController(SimpMessagingTemplate messagingTemplate, GameHandler gameHandler) {
        this.messagingTemplate = messagingTemplate;
        this.lobbyService = new LobbyService(gameHandler);
    }


    public LobbyService getLobbyService() {
        return lobbyService;
    }

    @MessageMapping("/lobby")
    public void handleLobbyMessage(@Payload LobbyMessage message) {
        System.out.println("Empfangen (Lobby): " + message.getType());

        List<LobbyMessage> responses = lobbyService.handle(message);

        // Alle Spieler erhalten dieselbe Nachricht
        for (LobbyMessage response : responses) {
            messagingTemplate.convertAndSend("/topic/lobby", response);
        }
    }


}
