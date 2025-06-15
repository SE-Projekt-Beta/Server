package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GameStateBroadcaster {
    private static final Logger logger = LoggerFactory.getLogger(GameStateBroadcaster.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final GameManager gameManager;

    @Autowired
    public GameStateBroadcaster(SimpMessagingTemplate messagingTemplate, GameManager gameManager) {
        this.messagingTemplate = messagingTemplate;
        this.gameManager = gameManager;
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastGameStates() {
        Map<Integer, GameHandler> handlers = gameManager.getHandlers();
        for (Map.Entry<Integer, GameHandler> entry : handlers.entrySet()) {
            int lobbyId = entry.getKey();
            GameHandler handler = entry.getValue();
            GameState gameState = handler.getGameState();
            if (gameState != null) {
                GameMessage gameStateMsg = new GameMessage(lobbyId, MessageType.GAME_STATE, gameState);
                messagingTemplate.convertAndSend("/topic/dkt/" + lobbyId, gameStateMsg);
                logger.debug("Broadcasted game state to lobby {}", lobbyId);
            }
        }
    }
}

