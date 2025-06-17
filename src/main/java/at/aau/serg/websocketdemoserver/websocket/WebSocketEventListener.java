package at.aau.serg.websocketdemoserver.websocket;

import at.aau.serg.websocketdemoserver.service.GameManager;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Integer userId = SessionUserRegistry.getUserId(sessionId);
        Integer gameId = SessionUserRegistry.getGameId(sessionId);
        if (userId != null) {
            // Optionally, you can log or handle the user ID if needed
            System.out.println("User ID for session " + sessionId + ": " + userId +
                               " in game " + gameId);
            // remove them from the game
            if (gameId != null) {
                // Notify the game handler about the user disconnecting
                GameManager.getInstance().getHandler(gameId).removePlayer(userId);
            }
        }
        SessionUserRegistry.unregister(sessionId);
    }
}

