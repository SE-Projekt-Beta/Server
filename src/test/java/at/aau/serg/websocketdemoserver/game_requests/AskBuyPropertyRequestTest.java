package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.AskBuyPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.StreetTileFactory;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.AskBuyPropertyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskBuyPropertyRequestTest {

    private GameState gameState;
    private AskBuyPropertyRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new AskBuyPropertyRequest();
    }

    @Test
    void testPlayerNotFound() {
        AskBuyPropertyPayload payload = new AskBuyPropertyPayload(999, 0, "", 0);
        GameMessage message = new GameMessage(MessageType.CAN_BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }

    @Test
    void testPlayerNotCurrent() {
        Player p1 = gameState.addPlayer("A");
        Player p2 = gameState.addPlayer("B");
        gameState.getPlayers(); // currentPlayer is p1
        p2.setCurrentTile(StreetTileFactory.createStreetTile(2)); // gültige kaufbare Straße
        AskBuyPropertyPayload payload = new AskBuyPropertyPayload(p2.getId(), 2, "Amtsplatz", 220);
        GameMessage message = new GameMessage(MessageType.CAN_BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ist nicht am Zug"));
    }

    @Test
    void testTileIsNotStreet() {
        Player p = gameState.addPlayer("A");
        gameState.getPlayers(); // p ist currentPlayer
        p.setCurrentTile(null); // kein Feld gesetzt
        AskBuyPropertyPayload payload = new AskBuyPropertyPayload(p.getId(), 0, "", 0);
        GameMessage message = new GameMessage(MessageType.CAN_BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("keine kaufbare Straße"));
    }

    @Test
    void testStreetAlreadyOwned() {
        Player p = gameState.addPlayer("A");
        gameState.getPlayers(); // p ist currentPlayer
        StreetTile tile = StreetTileFactory.createStreetTile(2);
        tile.setOwner(p); // bereits im Besitz
        p.setCurrentTile(tile);
        AskBuyPropertyPayload payload = new AskBuyPropertyPayload(p.getId(), 2, "Amtsplatz", 220);
        GameMessage message = new GameMessage(MessageType.CAN_BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("bereits im Besitz"));
    }

    @Test
    void testCanBuyProperty() {
        Player p = gameState.addPlayer("A");
        gameState.getPlayers(); // p ist currentPlayer
        StreetTile tile = StreetTileFactory.createStreetTile(2);
        p.setCurrentTile(tile);
        AskBuyPropertyPayload payload = new AskBuyPropertyPayload(p.getId(), 2, "Amtsplatz", 220);
        GameMessage message = new GameMessage(MessageType.CAN_BUY_PROPERTY, payload);
        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.CAN_BUY_PROPERTY, result.getType());
        AskBuyPropertyPayload response = result.parsePayload(AskBuyPropertyPayload.class);
        assertEquals(2, response.getTilePos());
        assertEquals("Amtsplatz", response.getTileName());
        assertEquals(220, response.getPrice());
    }
}
