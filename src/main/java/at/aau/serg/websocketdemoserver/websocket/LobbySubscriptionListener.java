package at.aau.serg.websocketdemoserver.websocket;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.LobbyUpdatePayload;
import at.aau.serg.websocketdemoserver.service.LobbyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Component
public class LobbySubscriptionListener implements ApplicationListener<SessionSubscribeEvent> {
    private static final Logger logger = LoggerFactory.getLogger(LobbySubscriptionListener.class);
    private static final String LOBBY_GENERAL_TOPIC = "/topic/lobby";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LobbyService lobbyService;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String destination = sha.getDestination();
        if (destination == null) return;
        if (destination.equals(LOBBY_GENERAL_TOPIC)) {
            try {
                // Send LIST_LOBBIES message
                LobbyMessage listLobbiesRequest = new LobbyMessage(LobbyMessageType.LIST_LOBBIES, null);
                List<LobbyMessage> responses = lobbyService.handle(listLobbiesRequest);
                for (LobbyMessage resp : responses) {
                    messagingTemplate.convertAndSend(LOBBY_GENERAL_TOPIC, resp);
                    logger.debug("Sent list message to {}: {}", LOBBY_GENERAL_TOPIC, resp);
                }
                // Send LOBBY_UPDATE for each lobby
                // We assume the lobby list is in the payload of a LOBBY_LIST message
                for (LobbyMessage resp : responses) {
                    if (resp.getType() == LobbyMessageType.LOBBY_LIST && resp.getPayload() instanceof List) {
                        List<?> lobbyList = (List<?>) resp.getPayload();
                        for (Object lobbyObj : lobbyList) {
                            logger.debug("Processing lobby object: {}", lobbyObj);
                            if (lobbyObj instanceof java.util.Map map && map.containsKey("lobbyId")) {
                                int lobbyId = (int) map.get("lobbyId");
                                // Fetch the lobby's players for the update payload
                                var lobby = lobbyService.getLobbyManager().getLobby(lobbyId);
                                if (lobby != null) {
                                    LobbyMessage updateMsg = new LobbyMessage(lobbyId, LobbyMessageType.LOBBY_UPDATE,
                                        new LobbyUpdatePayload(lobbyId, lobby.getPlayers()));
                                    messagingTemplate.convertAndSend(LOBBY_GENERAL_TOPIC, updateMsg);
                                    logger.debug("Sent lobby update message to {}: {}", LOBBY_GENERAL_TOPIC, updateMsg);
                                }
                            }
                        }
                    }
                }
                logger.info("Sent LIST_LOBBIES and LOBBY_UPDATEs to {} on subscribe", LOBBY_GENERAL_TOPIC);
            } catch (Exception e) {
                logger.warn("Failed to send LIST_LOBBIES/LOBBY_UPDATE on subscribe: {}", e.getMessage());
            }
        }
    }
}
