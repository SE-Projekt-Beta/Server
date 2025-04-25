package at.aau.serg.websocketdemoserver.websocket;

import at.aau.serg.websocketdemoserver.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
public class LobbyWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private static final String ENDPOINT = "/websocket-example-broker";
    private static final String SEND_PATH = "/app/lobby";
    private static final String SUBSCRIBE_PATH = "/topic/lobby";

    private BlockingQueue<LobbyMessage> messages;

    @BeforeEach
    void setup() {
        messages = new LinkedBlockingDeque<>();
    }

    @Test
    void testJoinLobbyMessageIsHandledAndBroadcasted() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient.connect(
                "ws://localhost:" + port + ENDPOINT,
                new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe(SUBSCRIBE_PATH, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return LobbyMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.offer((LobbyMessage) payload);
            }
        });

        LobbyMessage join = new LobbyMessage();
        join.setType(LobbyMessageType.JOIN_LOBBY);
        join.setPayload(new JoinLobbyPayload("Thomas"));

        session.send(SEND_PATH, join);

        LobbyMessage received = messages.poll(2, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.getType()).isEqualTo(LobbyMessageType.LOBBY_UPDATE);
    }

    @Test
    void testStartGameMessageIsHandledAndBroadcasted() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient.connect(
                "ws://localhost:" + port + ENDPOINT,
                new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe(SUBSCRIBE_PATH, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return LobbyMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.offer((LobbyMessage) payload);
            }
        });

        session.send(SEND_PATH, new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("Thomas")));
        session.send(SEND_PATH, new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("David")));
        Thread.sleep(300);

        session.send(SEND_PATH, new LobbyMessage(LobbyMessageType.START_GAME, null));

        LobbyMessage startGameMsg = null;
        for (int i = 0; i < 5; i++) {
            LobbyMessage m = messages.poll(2, TimeUnit.SECONDS);
            if (m != null && m.getType() == LobbyMessageType.START_GAME) {
                startGameMsg = m;
                break;
            }
        }

        assertThat(startGameMsg).isNotNull();

        // 👉 Manuelles Mapping
        ObjectMapper mapper = new ObjectMapper();
        GameStartPayload startPayload = mapper.convertValue(startGameMsg.getPayload(), GameStartPayload.class);

        assertThat(startPayload).isNotNull();
        assertThat(startPayload.getPlayerOrder()).hasSize(2);
        assertThat(startPayload.getPlayerOrder())
                .extracting("username")
                .containsExactlyInAnyOrder("Thomas", "David");
    }
}
