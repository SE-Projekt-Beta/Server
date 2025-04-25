package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.Tile;
import at.aau.serg.websocketdemoserver.model.tiles.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameHandlerTest {

    private GameHandler handler;

    @BeforeEach
    void setup() {
        handler = new GameHandler();
    }

    @Test
    void testHandleRollDiceSuccess() throws JSONException {
        JSONObject json = new JSONObject().put("playerId", "p1");
        GameMessage result = handler.handle(new GameMessage(MessageType.ROLL_DICE, json.toString()));
        assertEquals(MessageType.PLAYER_MOVED, result.getType());
        assertFalse(handler.getExtraMessages().isEmpty());
    }

    @Test
    void testHandleRollDiceInvalidPayload() {
        GameMessage result = handler.handle(new GameMessage(MessageType.ROLL_DICE, "invalid"));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testHandleBuyPropertySuccess() throws JSONException {
        JSONObject payload = new JSONObject().put("playerId", "p1").put("tilePos", 1);
        GameMessage result = handler.handle(new GameMessage(MessageType.BUY_PROPERTY, payload.toString()));
        assertEquals(MessageType.PROPERTY_BOUGHT, result.getType());
    }

    @Test
    void testHandleBuyPropertyAlreadyOwned() throws JSONException {
        JSONObject payload = new JSONObject().put("playerId", "p1").put("tilePos", 1);
        handler.handle(new GameMessage(MessageType.BUY_PROPERTY, payload.toString())); // first buy
        GameMessage result = handler.handle(new GameMessage(MessageType.BUY_PROPERTY, payload.toString())); // second buy
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testHandleBuyPropertyInvalidPayload() {
        GameMessage result = handler.handle(new GameMessage(MessageType.BUY_PROPERTY, "invalid"));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testDecideActionWithEventBank() {
        Tile tile = new GeneralEventTile(6, "Bank Ereignis");
        GameMessage result = handler.decideAction("p1", tile);
        assertEquals(MessageType.DRAW_EVENT_BANK_CARD, result.getType());
    }

    @Test
    void testDecideActionWithEventRisiko() {
        Tile tile = new GeneralEventTile(2, "Risiko Ereignis");
        GameMessage result = handler.decideAction("p1", tile);
        assertEquals(MessageType.DRAW_EVENT_RISIKO_CARD, result.getType());
    }

    @Test
    void testDecideActionWithEventNeutral() {
        Tile tile = new GeneralEventTile(3, "Ereignis");
        GameMessage result = handler.decideAction("p1", tile);
        assertEquals(MessageType.SKIPPED, result.getType());
    }

    @Test
    void testDecideActionGoToJail() {
        Tile tile = new GoToJail(30, "Geh ins Gefängnis");
        GameMessage result = handler.decideAction("p1", tile);
        assertEquals(MessageType.GO_TO_JAIL, result.getType());
    }

    @Test
    void testDecideActionFreeTiles() {
        assertEquals(MessageType.SKIPPED, handler.decideAction("p1", new Free(20, "Frei Parken")).getType());
        assertEquals(MessageType.SKIPPED, handler.decideAction("p1", new Jail(10, "Gefängnis")).getType());
        assertEquals(MessageType.SKIPPED, handler.decideAction("p1", new Start(0, "Los")).getType());
    }

    @Test
    void testDecideActionTax() {
        Tile tile = new Tax(4, "Einkommensteuer", 100);
        GameMessage result = handler.decideAction("p1", tile);
        assertEquals(MessageType.PAY_TAX, result.getType());
    }

    @Test
    void testDecideActionBuyableProperty() {
        Tile tile = new Street(1, "Teststraße", 200, 50, 100);
        GameMessage result = handler.decideAction("p1", tile);
        assertEquals(MessageType.CAN_BUY_PROPERTY, result.getType());
    }

    @Test
    void testDecideActionMustPayRent() throws JSONException {
        Tile tile = new Street(1, "Teststraße", 200, 50, 100);
        JSONObject payload = new JSONObject().put("playerId", "owner").put("tilePos", 1);
        handler.handle(new GameMessage(MessageType.BUY_PROPERTY, payload.toString()));
        GameMessage result = handler.decideAction("pX", tile);
        assertEquals(MessageType.MUST_PAY_RENT, result.getType());
    }

    @Test
    void testHandleUnknownMessageType() {
        GameMessage result = handler.handle(new GameMessage(null, null));
        assertEquals(MessageType.ERROR, result.getType());
    }

}
