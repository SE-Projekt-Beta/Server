package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameHandlerTest {

    private GameState gameState;
    private GameHandler handler;

    @BeforeEach
    void setup() {
        gameState = new GameState();
        handler = new GameHandler(gameState);
    }

    @Test
    void testInitGameAddsStartMessage() {
        // Dummy-Tiles mit Typ
        Tile tile = new Tile(0) {
            @Override
            public TileType getType() {
                return TileType.SPECIAL;
            }
        };

        Player p1 = new Player(1, "Max", tile);
        Player p2 = new Player(2, "Eva", tile);

        handler.initGame(List.of(p1, p2));

        List<GameMessage> extras = handler.getExtraMessages();
        assertEquals(1, extras.size());

        GameMessage msg = extras.get(0);
        assertEquals(MessageType.START_GAME, msg.getType());

        GameStartedPayload payload = (GameStartedPayload) msg.getPayload();
        assertEquals(2, payload.getPlayerOrder().size());
        assertEquals("Max", payload.getPlayerOrder().get(0).getNickname());
    }

    @Test
    void testHandleReturnsErrorIfNullMessage() {
        GameMessage result = handler.handle(null);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testHandleReturnsErrorIfUnknownType() {
        GameMessage msg = new GameMessage();
        msg.setType(null);
        GameMessage result = handler.handle(msg);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testHandleReturnsErrorIfUnmappedType() {
        GameMessage msg = new GameMessage();
        msg.setType(MessageType.END_GAME); // Wird im Handler nicht unterstützt
        GameMessage result = handler.handle(msg);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testGetGameStateReturnsCorrectInstance() {
        assertEquals(gameState, handler.getGameState());
    }

    @Test
    void testExtraMessagesClearedOnNewHandleCall() {
        // Zuerst initGame ausführen → 1 extraMessage
        Tile tile = new Tile(0) {
            @Override
            public TileType getType() {
                return TileType.SPECIAL;
            }
        };
        handler.initGame(List.of(new Player(1, "Max", tile)));
        assertFalse(handler.getExtraMessages().isEmpty());

        // Jetzt handle() mit ungültiger Message → extraMessages wird geleert
        GameMessage msg = new GameMessage();
        msg.setType(MessageType.END_GAME);
        handler.handle(msg);

        assertTrue(handler.getExtraMessages().isEmpty());
    }
}
