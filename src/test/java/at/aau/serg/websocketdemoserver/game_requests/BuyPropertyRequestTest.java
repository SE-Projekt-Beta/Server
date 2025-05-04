package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.BuyPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.StreetTileFactory;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.BuyPropertyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuyPropertyRequestTest {

    private GameState gameState;
    private BuyPropertyRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new BuyPropertyRequest();
    }

    @Test
    void testPlayerNotFound() {
        BuyPropertyPayload payload = new BuyPropertyPayload(999, 2, "Amtsplatz", 220);
        GameMessage message = new GameMessage(MessageType.BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }

    @Test
    void testInvalidTile() {
        Player player = gameState.addPlayer("Test");
        BuyPropertyPayload payload = new BuyPropertyPayload(player.getId(), 99, "Invalid", 0);
        GameMessage message = new GameMessage(MessageType.BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Kein kaufbares Feld"));
    }

    @Test
    void testPurchaseFails() {
        Player player = gameState.addPlayer("Test");
        // Spieler hat z. B. zu wenig Geld
        StreetTile tile = StreetTileFactory.createStreetTile(2);
        tile.setOwner(null);  // noch frei
        player.setCash(0); // kein Geld
        gameState.getBoard().getTile(2); // sicherstellen, dass Feld existiert
        BuyPropertyPayload payload = new BuyPropertyPayload(player.getId(), 2, tile.getLabel(), tile.getPrice());
        GameMessage message = new GameMessage(MessageType.BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Kauf nicht möglich"));
    }

    @Test
    void testSuccessfulPurchase() {
        Player player = gameState.addPlayer("Test");
        StreetTile tile = StreetTileFactory.createStreetTile(2);
        gameState.getBoard().getTiles().set(2, tile); // ersetze das Tile im Board
        player.setCash(10000); // genug Geld
        BuyPropertyPayload payload = new BuyPropertyPayload(player.getId(), 2, tile.getLabel(), tile.getPrice());
        GameMessage message = new GameMessage(MessageType.BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.PROPERTY_BOUGHT, result.getType());
        BuyPropertyPayload resultPayload = result.parsePayload(BuyPropertyPayload.class);
        assertEquals(player.getId(), resultPayload.getPlayerId());
        assertEquals(2, resultPayload.getTilePosition());
        assertEquals(tile.getLabel(), resultPayload.getTileName());
        assertEquals(tile.getPrice(), resultPayload.getPrice());
    }
}
