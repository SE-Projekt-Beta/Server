package at.aau.serg.websocketdemoserver.websocket;


import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
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

        GameMessage testMsg = new GameMessage();
        testMsg.setType(MessageType.ROLL_DICE);
        testMsg.setPayload("{\"playerId\": \"playerTest\"}");

        session.send(SEND_PATH, testMsg);

        GameMessage received = messages.poll(2, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.getType()).isEqualTo(MessageType.PLAYER_MOVED);
    }
}

