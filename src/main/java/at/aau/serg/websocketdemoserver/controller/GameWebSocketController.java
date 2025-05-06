package at.aau.serg.websocketdemoserver.controller;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.Lobby;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyManager lobbyManager;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   LobbyManager lobbyManager) {
        this.messagingTemplate = messagingTemplate;
        this.lobbyManager = lobbyManager;
    }

    /**
     * Clients send to /app/lobby/{lobbyId}/game
     * Clients should subscribe to /topic/lobby/{lobbyId}/game
     */
    @MessageMapping("/lobby/{lobbyId}/game")
    public void handleGameMessage(@DestinationVariable String lobbyId,
                                  @Payload GameMessage message) {

        // 1) Look up the lobby
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            // optionally send an error back
            return;
        }

        // 2) Dispatch to *this* lobby's handler
        GameMessage response = lobby.getGameHandler().handle(message);

        // 3) Broadcast the primary response
        messagingTemplate.convertAndSend(
                "/topic/lobby/" + lobbyId + "/game",
                response
        );

        // 4) Broadcast any extra messages (e.g. START_GAME payloads)
        for (GameMessage extra : lobby.getGameHandler().getExtraMessages()) {
            messagingTemplate.convertAndSend(
                    "/topic/lobby/" + lobbyId + "/game",
                    extra
            );
        }
    }
}
