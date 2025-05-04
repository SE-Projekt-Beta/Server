package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {

    private LobbyService lobbyService;
    private GameHandler gameHandler;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new at.aau.serg.websocketdemoserver.model.gamestate.GameBoard());
        gameHandler = new GameHandler(gameState);
        lobbyService = new LobbyService(gameState, gameHandler);
    }

    @Test
    void testJoinLobby_validNickname() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, "Alice");
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.get(0).getType());
    }

    @Test
    void testJoinLobby_invalidNickname() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.JOIN_LOBBY, "");
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }

    @Test
    void testLeaveLobby_validNickname() {
        gameState.addPlayer("Bob");

        LobbyMessage msg = new LobbyMessage(LobbyMessageType.LEAVE_LOBBY, "Bob");
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.get(0).getType());
    }

    @Test
    void testLeaveLobby_invalidNickname() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.LEAVE_LOBBY, "");
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }

    @Test
    void testStartGame_notEnoughPlayers() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.START_GAME, null);
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }

    @Test
    void testStartGame_valid() {
        gameState.addPlayer("A");
        gameState.addPlayer("B");

        LobbyMessage msg = new LobbyMessage(LobbyMessageType.START_GAME, null);
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.START_GAME, result.get(0).getType());
    }

    @Test
    void testLobbyUpdate() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, null);
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.get(0).getType());
    }

    @Test
    void testPlayerInit_valid() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.PLAYER_INIT, "PlayerX");
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.PLAYER_INIT, result.get(0).getType());
    }

    @Test
    void testPlayerInit_invalid() {
        LobbyMessage msg = new LobbyMessage(LobbyMessageType.PLAYER_INIT, "");
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }

    @Test
    void testUnknownMessageType() {
        LobbyMessage msg = new LobbyMessage(null, "Test");
        List<LobbyMessage> result = lobbyService.handle(msg);

        assertEquals(1, result.size());
        assertEquals(LobbyMessageType.ERROR, result.get(0).getType());
    }
}
