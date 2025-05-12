package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static at.aau.serg.websocketdemoserver.dto.MessageType.*;
import static org.junit.jupiter.api.Assertions.*;

class GameHandlerTest {

    private GameState gameState;
    private GameHandler handler;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        handler = new GameHandler(gameState);
        PlayerDTO p1 = new PlayerDTO(1, "Alice");
        PlayerDTO p2 = new PlayerDTO(2, "Bob");
        handler.initGame(List.of(p1, p2));
    }

    @Test
    void testHandleNullMessage() {
        GameMessage result = handler.handle(null);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testHandleNullType() {
        GameMessage msg = new GameMessage(1, null, null);
        GameMessage result = handler.handle(msg);
        assertEquals(MessageType.ERROR, result.getType());
    }


    @Test
    void testHandleRequestGameState() {
        GameMessage msg = new GameMessage(1, REQUEST_GAME_STATE, null);
        GameMessage result = handler.handle(msg);
        assertEquals(MessageType.GAME_STATE, result.getType());
    }

    @Test
    void testInitGameAndGetCurrentPlayerId() {
        String current = handler.getCurrentPlayerId();
        assertEquals("1", current); // erste ID der Liste
    }

    @Test
    void testGetExtraMessagesInitiallyEmpty() {
        List<GameMessage> messages = handler.getExtraMessages();
        assertTrue(messages.isEmpty());
    }

    @Test
    void testExecuteRollDice() {
        GameMessage input = new GameMessage(1, ROLL_DICE, Map.of("playerId", 1));
        GameMessage response = handler.handle(input);

        assertEquals(MessageType.GAME_STATE, response.getType());
        assertFalse(handler.getExtraMessages().isEmpty());
    }

    @Test
    void testExecuteBuyPropertyFailGracefully() {
        GameMessage input = new GameMessage(1, BUY_PROPERTY, Map.of("playerId", 1, "tilePos", 999)); // ungültig
        GameMessage response = handler.handle(input);

        assertEquals(MessageType.ERROR, response.getType());
    }


}
