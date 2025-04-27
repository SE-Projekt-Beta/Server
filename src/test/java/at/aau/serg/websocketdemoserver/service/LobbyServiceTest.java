package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import at.aau.serg.websocketdemoserver.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {

    private LobbyService lobbyService;

    @BeforeEach
    void setup() {
        lobbyService = new LobbyService(new GameHandler());
    }

    @Test
    void testHandleJoinLobbySuccess() {
        JoinLobbyPayload payload = new JoinLobbyPayload("TestUser");
        LobbyMessage joinMessage = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, payload);

        List<LobbyMessage> responses = lobbyService.handle(joinMessage);

        assertEquals(1, responses.size());
        LobbyMessage response = responses.get(0);
        assertEquals(LobbyMessageType.LOBBY_UPDATE, response.getType());

        LobbyUpdatePayload update = (LobbyUpdatePayload) response.getPayload();
        assertNotNull(update);
        assertEquals(1, update.getPlayers().size());
        assertEquals("TestUser", update.getPlayers().get(0).getNickname());
    }

    @Test
    void testHandleJoinLobbyInvalidPayload() {
        LobbyMessage joinMessage = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, "InvalidPayload");

        List<LobbyMessage> responses = lobbyService.handle(joinMessage);

        assertEquals(1, responses.size());
        assertEquals(LobbyMessageType.ERROR, responses.get(0).getType());
    }

    @Test
    void testHandleStartGameNotEnoughPlayers() {
        LobbyMessage startMessage = new LobbyMessage(LobbyMessageType.START_GAME, null);

        List<LobbyMessage> responses = lobbyService.handle(startMessage);

        assertEquals(1, responses.size());
        assertEquals(LobbyMessageType.ERROR, responses.get(0).getType());
        assertTrue(responses.get(0).getPayload().toString().contains("Mindestens 2 Spieler"));
    }

    @Test
    void testHandleStartGameSuccess() {
        // Zwei Spieler hinzufügen
        lobbyService.handle(new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("User1")));
        lobbyService.handle(new LobbyMessage(LobbyMessageType.JOIN_LOBBY, new JoinLobbyPayload("User2")));

        LobbyMessage startMessage = new LobbyMessage(LobbyMessageType.START_GAME, null);

        List<LobbyMessage> responses = lobbyService.handle(startMessage);

        assertEquals(1, responses.size());
        LobbyMessage response = responses.get(0);
        assertEquals(LobbyMessageType.START_GAME, response.getType());

        GameStartPayload payload = (GameStartPayload) response.getPayload();
        assertNotNull(payload);
        assertEquals(2, payload.getPlayerOrder().size());
    }

    @Test
    void testHandleUnknownType() {
        LobbyMessage unknownMessage = new LobbyMessage(null, null);

        List<LobbyMessage> responses = lobbyService.handle(unknownMessage);

        assertEquals(1, responses.size());
        assertEquals(LobbyMessageType.ERROR, responses.get(0).getType());
    }
}