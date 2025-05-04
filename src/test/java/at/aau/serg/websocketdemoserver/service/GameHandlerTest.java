package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameHandlerTest {

    private GameState gameState;
    private GameHandler handler;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new at.aau.serg.websocketdemoserver.model.gamestate.GameBoard());
        handler = new GameHandler(gameState);
    }

    @Test
    void testHandle_NullMessage() {
        GameMessage result = handler.handle(null);
        assertEquals(MessageType.ERROR, result.getType());
        assertEquals("Ungültige Nachricht.", result.getPayload());
    }

    @Test
    void testHandle_NullType() {
        GameMessage message = new GameMessage();
        GameMessage result = handler.handle(message);
        assertEquals(MessageType.ERROR, result.getType());
        assertEquals("Ungültige Nachricht.", result.getPayload());
    }


    @Test
    void testInitGame() {
        Player p1 = gameState.addPlayer("Max");
        Player p2 = gameState.addPlayer("Eva");
        handler.initGame(List.of(p1, p2));
        assertEquals(2, handler.getGameState().getPlayers().size());
    }

    @Test
    void testGetExtraMessages() {
        assertNotNull(handler.getExtraMessages());
        assertTrue(handler.getExtraMessages().isEmpty());
    }

    @Test
    void testGetGameState() {
        assertEquals(gameState, handler.getGameState());
    }
}
