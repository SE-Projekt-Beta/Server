package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BuyPropertyRequestTest {

    private BuyPropertyRequest request;
    private GameState gameState;
    private Player player;
    private StreetTile tile;
    private int lobbyId;

    @BeforeEach
    void setUp() {
        request = new BuyPropertyRequest();
        gameState = new GameState();
        GameBoard board = gameState.getBoard();

        player = new Player("Buyer", board);
        player.setCash(1000);
        gameState.startGame(List.of(player));
        lobbyId = 1;

        tile = new StreetTile(2, "Teststraße", 300, 50, null, 100);
        board.getTiles().add(tile);
    }

    private Map<String, Object> buildPayload(int playerId, int tilePos) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", playerId);
        payload.put("tilePos", tilePos);
        return payload;
    }


    @Test
    void testInvalidPlayer() {
        GameMessage msg = request.execute(lobbyId, buildPayload(999, tile.getIndex()), gameState, new ArrayList<>());
        assertEquals("ERROR", msg.getType().name());
        assertTrue(msg.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testTileIsNotStreet() {
        var nonStreetTile = new DummyTile(99); // Tile that is not StreetTile
        gameState.getBoard().getTiles().add(nonStreetTile);

        GameMessage msg = request.execute(lobbyId, buildPayload(player.getId(), 99), gameState, new ArrayList<>());
        assertEquals("ERROR", msg.getType().name());
        assertTrue(msg.getPayload().toString().contains("nicht kaufbar"));
    }


    @Test
    void testPurchaseFailsDueToCash() {
        player.setCash(100); // not enough
        GameMessage msg = request.execute(lobbyId, buildPayload(player.getId(), tile.getIndex()), gameState, new ArrayList<>());
        assertEquals("ERROR", msg.getType().name());
        assertTrue(msg.getPayload().toString().contains("Kauf fehlgeschlagen"));
    }


    @Test
    void testExceptionHandling() {
        Map<String, Object> brokenPayload = Map.of("tilePos", 2); // missing playerId
        GameMessage msg = request.execute(lobbyId, brokenPayload, gameState, new ArrayList<>());
        assertEquals("ERROR", msg.getType().name());
        assertTrue(msg.getPayload().toString().contains("Fehler beim Kaufen"));
    }

    // DummyTile used to simulate non-StreetTile
    static class DummyTile extends Tile {
        public DummyTile(int index) {
            super(index);
        }

        @Override
        public TileType getType() {
            return TileType.BANK;
        }
    }
}
