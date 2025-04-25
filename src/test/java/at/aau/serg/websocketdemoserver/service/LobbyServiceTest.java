package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTest {

    private LobbyService service;

    @BeforeEach
    void setup() {
        service = new LobbyService(new GameHandler());
    }

    @Test
    void testJoinLobbyAddsPlayerAndReturnsUpdate() {
        JoinLobbyPayload payload = new JoinLobbyPayload("Alice");
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, payload);

        List<LobbyMessage> response = service.handle(msg);
        assertEquals(1, response.size());
        assertEquals(LobbyMessageType.LOBBY_UPDATE, response.get(0).getType());

        LobbyUpdatePayload update = (LobbyUpdatePayload) response.get(0).getPayload();
        assertEquals(1, update.getPlayers().size());
        assertEquals("Alice", update.getPlayers().get(0).getUsername());
    }

    @Test
    void testJoinLobbyTwiceReturnsSamePlayerId() {
        JoinLobbyPayload payload = new JoinLobbyPayload("Bob");

        LobbyMessage msg = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, payload);
        List<LobbyMessage> firstJoin = service.handle(msg);
        List<LobbyMessage> secondJoin = service.handle(msg);

        LobbyUpdatePayload update1 = (LobbyUpdatePayload) firstJoin.get(0).getPayload();
        LobbyUpdatePayload update2 = (LobbyUpdatePayload) secondJoin.get(0).getPayload();

        String id1 = update1.getPlayers().get(0).getId();
        String id2 = update2.getPlayers().get(0).getId();
        assertEquals(id1, id2);
    }

    @Test
    void testStartGameFailsIfNotEnoughPlayers() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.START_GAME, null);
        List<LobbyMessage> result = service.handle(msg);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }

    @Test
    void testStartGameSucceedsWithEnoughPlayers() {
        service.handle(new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("Alice")));
        service.handle(new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("Bob")));

        List<LobbyMessage> result = service.handle(new LobbyMessage(LobbyMessageType.START_GAME, null));

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.START_GAME, result.get(0).getType());

        GameStartPayload payload = (GameStartPayload) result.get(0).getPayload();
        assertEquals(2, payload.getPlayerOrder().size());
    }

    @Test
    void testInvalidMessageTypeReturnsError() {
        LobbyMessage msg = new LobbyMessage(null, null);
        List<LobbyMessage> result = service.handle(msg);
        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }
}
