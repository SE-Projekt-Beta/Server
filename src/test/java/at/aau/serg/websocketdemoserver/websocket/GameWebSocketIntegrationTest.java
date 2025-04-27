package at.aau.serg.websocketdemoserver.websocket;

import at.aau.serg.websocketdemoserver.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private static final String ENDPOINT = "/websocket-example-broker";
    private static final String SEND_PATH = "/app/dkt";
    private static final String LOBBY_PATH = "/app/lobby";
    private static final String SUBSCRIBE_PATH = "/topic/dkt";

    BlockingQueue<GameMessage> messages = new LinkedBlockingDeque<>();

    @Test
    void testRollDiceMessageIsHandledAndBroadcasted() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient.connectAsync("ws://localhost:" + port + ENDPOINT,
                        new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        session.subscribe(SUBSCRIBE_PATH, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GameMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.offer((GameMessage) payload);
            }
        });

        // Lobby beitreten
        session.send(LOBBY_PATH, new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("TestPlayer")));
        Thread.sleep(200);

        session.send(LOBBY_PATH, new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("TestPlayer2")));
        Thread.sleep(200);

        // Spiel starten
        session.send(LOBBY_PATH, new LobbyMessage(LobbyMessageType.START_GAME, null));
        Thread.sleep(200);

        // Warten bis CURRENT_PLAYER empfangen wird
        GameMessage currentPlayerMessage = waitForMessageType(MessageType.CURRENT_PLAYER, 5);
        assertThat(currentPlayerMessage).isNotNull();

        // PlayerId extrahieren
        Map<String, Object> payloadMap = (Map<String, Object>) currentPlayerMessage.getPayload();
        String currentPlayerId = payloadMap.get("playerId").toString();

        // Roll Dice senden
        GameMessage rollDiceMessage = new GameMessage();
        rollDiceMessage.setType(MessageType.ROLL_DICE);
        rollDiceMessage.setPayload("{\"playerId\": " + currentPlayerId + "}");

        session.send(SEND_PATH, rollDiceMessage);

        // Warten auf PLAYER_MOVED
        GameMessage movedMessage = waitForMessageType(MessageType.PLAYER_MOVED, 5);
        assertThat(movedMessage).isNotNull();
    }

    private GameMessage waitForMessageType(MessageType expectedType, int timeoutSeconds) throws InterruptedException {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < end) {
            GameMessage msg = messages.poll(500, TimeUnit.MILLISECONDS);
            if (msg != null && msg.getType() == expectedType) {
                return msg;
            }
        }
        return null;
    }
}
