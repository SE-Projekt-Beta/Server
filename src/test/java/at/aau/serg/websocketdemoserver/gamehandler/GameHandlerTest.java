package at.aau.serg.websocketdemoserver.gamehandler;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.tiles.Event;
import at.aau.serg.websocketdemoserver.model.tiles.Free;
import at.aau.serg.websocketdemoserver.model.tiles.Street;
import at.aau.serg.websocketdemoserver.model.tiles.Tax;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GameHandlerTest {
    @Test
    void testHandleRollDiceReturnsPlayerMoved() {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\": \"player1\"}";
        GameMessage input = new GameMessage(MessageType.ROLL_DICE, payload);

        GameMessage result = handler.handle(input);

        assertNotNull(result);
        assertEquals(MessageType.PLAYER_MOVED, result.getType());
        assertTrue(result.getPayload() instanceof PlayerMovePayload);
        PlayerMovePayload movePayload = (PlayerMovePayload) result.getPayload();
        assertEquals("player1", movePayload.getPlayerId());
    }

    @Test
    void testPlayerMovedIncludesTileData() throws JSONException {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"player1\"}";

        GameMessage result = handler.handle(new GameMessage(MessageType.ROLL_DICE, payload));

        assertEquals(MessageType.PLAYER_MOVED, result.getType());

        PlayerMovePayload movePayload = (PlayerMovePayload) result.getPayload();
        assertNotNull(movePayload.getTileName());
        assertNotNull(movePayload.getTileType());
    }

    @Test
    void testTileTypeIsValid() throws JSONException {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"test\"}";
        GameMessage result = handler.handle(new GameMessage(MessageType.ROLL_DICE, payload));

        PlayerMovePayload movePayload = (PlayerMovePayload) result.getPayload();
        String type = movePayload.getTileType().toLowerCase();

        assertTrue(
                type.matches("start|street|station|event|event_risiko|event_bank|tax|jail|goto_jail|free"),
                "tileType ist kein gültiger Typ: " + type
        );
    }

    @Test
    void testPlayerPositionIsStored() {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"testPlayer\"}";
        handler.handle(new GameMessage(MessageType.ROLL_DICE, payload));

        int pos = handler.getGameState().getPosition("testPlayer");
        assertTrue(pos >= 0 && pos < 40, "Position liegt nicht im gültigen Bereich");
    }

    @Test
    void testMultipleRollsNoCrash() {
        GameHandler handler = new GameHandler();

        for (int i = 0; i < 10; i++) {
            String payload = "{\"playerId\":\"multi\"}";
            GameMessage result = handler.handle(new GameMessage(MessageType.ROLL_DICE, payload));
            assertEquals(MessageType.PLAYER_MOVED, result.getType());
        }

        int pos = handler.getGameState().getPosition("multi");
        assertTrue(pos >= 0 && pos < 40);
    }


    @Test
    void testCreateStreetTile() {
        Street tile = new Street(5, "Opernring", 220, 55, 110);
        assertEquals("Opernring", tile.getName());
        assertEquals(5, tile.getPosition());
        assertEquals("street", tile.getTileType());
        assertEquals(220, tile.getPrice());
        assertEquals(55, tile.getRent());
        assertEquals(110, tile.getHouseCost());
    }

    @Test
    void testDecideActionForStreet() {
        GameHandler handler = new GameHandler();
        Street tile = new Street(5, "Opernring", 220, 55, 110);

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals(MessageType.CAN_BUY_PROPERTY, msg.getType());

    }

    @Test
    void testDecideActionForTax() {
        GameHandler handler = new GameHandler();
        Tax tile = new Tax(4, "Einkommenssteuer", 100);

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals(MessageType.PAY_TAX, msg.getType());
    }

    @Test
    void testDecideActionForRisikoEvent() {
        GameHandler handler = new GameHandler();
        Event tile = new Event(2, "event_risiko");

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals(MessageType.DRAW_EVENT_RISIKO_CARD, msg.getType());
    }

    @Test
    void testDecdeActionForBankEvent() {
        GameHandler handler = new GameHandler();
        Event tile = new Event(2, "event_bank");

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals(MessageType.DRAW_EVENT_BANK_CARD, msg.getType());
    }

    @Test
    void testDecideActionForFreeField() {
        GameHandler handler = new GameHandler();
        Free tile = new Free(20, "Frei Parken");

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals(MessageType.SKIPPED, msg.getType());
    }

    @Test
    void testExtraActionGeneratedAfterRoll() {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"player1\"}";
        handler.handle(new GameMessage(MessageType.ROLL_DICE, payload));

        List<GameMessage> extras = handler.getExtraMessages();
        assertEquals(1, extras.size(), "Es sollte genau eine Aktionsnachricht geben");

        GameMessage action = extras.get(0);
        assertNotNull(action.getType(), "Aktionstyp darf nicht null sein");

        assertTrue(
                List.of(
                        MessageType.CAN_BUY_PROPERTY,
                        MessageType.PAY_TAX,
                        MessageType.DRAW_EVENT_RISIKO_CARD,
                        MessageType.DRAW_EVENT_BANK_CARD,
                        MessageType.GO_TO_JAIL,
                        MessageType.SKIPPED
                ).contains(action.getType()),
                "Unerwarteter Aktionstyp: " + action.getType()
        );
    }

    @Test
    void testBuyPropertyStoresOwnership() throws JSONException {
        GameHandler handler = new GameHandler();

        // Spieler "player1" kauft Feld Nr. 5
        BuyPropertyPayload payload = new BuyPropertyPayload("player1", 5);

        GameMessage result = handler.handle(new GameMessage(MessageType.BUY_PROPERTY, payload));

        assertEquals(MessageType.PROPERTY_BOUGHT, result.getType());
        assertEquals("player1", handler.getOwner(5), "Feld 5 sollte nun player1 gehören");
    }
}
