package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
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

    @Autowired
    public GameStateBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 2000)
    public void broadcastGameStates() {
        Map<Integer, GameHandler> handlers = GameManager.getInstance().getHandlers();
        for (Map.Entry<Integer, GameHandler> entry : handlers.entrySet()) {
            int lobbyId = entry.getKey();
            GameHandler handler = entry.getValue();
            GameState gameState = handler.getGameState();
            if (gameState != null) {
                GameMessage gameStateMsg = MessageFactory.gameState(lobbyId, gameState);
                messagingTemplate.convertAndSend("/topic/dkt/" + lobbyId, gameStateMsg);
                logger.debug("Broadcasted game state to lobby {}", lobbyId);
            }
        }
    }
}
