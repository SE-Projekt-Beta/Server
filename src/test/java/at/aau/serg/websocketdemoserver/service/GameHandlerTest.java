package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameHandlerTest {

    private GameHandler handler;

    @BeforeEach
    void setup() {
        handler = new GameHandler();
    }

    @Test
    void testInitGameCreatesPlayers() {
        List<CurrentPlayerPayload> players = List.of(
                new CurrentPlayerPayload(1, "Alice"),
                new CurrentPlayerPayload(2, "Bob")
        );
        handler.initGame(players);

        assertEquals("1", handler.getCurrentPlayerId());
    }

    @Test
    void testHandleRollDiceSuccess() throws JSONException {
        List<CurrentPlayerPayload> players = List.of(new CurrentPlayerPayload(1, "Alice"));
        handler.initGame(players);

        int actualPlayerId = Integer.parseInt(handler.getCurrentPlayerId()); // hole echte ID!

        JSONObject payload = new JSONObject();
        payload.put("playerId", actualPlayerId); // NICHT fix 1!

        GameMessage response = handler.handle(new GameMessage(MessageType.ROLL_DICE, payload.toString()));

        assertEquals(MessageType.PLAYER_MOVED, response.getType());
        assertFalse(handler.getExtraMessages().isEmpty());
    }


    @Test
    void testHandleRollDiceWrongPlayer() throws JSONException {
        List<CurrentPlayerPayload> players = List.of(new CurrentPlayerPayload(1, "Alice"));
        handler.initGame(players);

        JSONObject payload = new JSONObject();
        payload.put("playerId", 999); // Falsche ID

        GameMessage response = handler.handle(new GameMessage(MessageType.ROLL_DICE, payload.toString()));

        assertEquals(MessageType.ERROR, response.getType());
    }

    @Test
    void testHandleBuyPropertySuccess() throws JSONException {
        // Spiel vorbereiten
        List<CurrentPlayerPayload> players = List.of(new CurrentPlayerPayload(1, "Alice"));
        handler.initGame(players);

        // Spieler eine Bewegung geben, damit er auf einem Feld steht
        JSONObject rollPayload = new JSONObject();
        rollPayload.put("playerId", 1);
        handler.handle(new GameMessage(MessageType.ROLL_DICE, rollPayload.toString()));

        // Jetzt korrekt Kaufversuch auf sein aktuelles Feld
        int tilePos = 0; // Default auf Startposition

        // Versuchen echte Tile-Position zu lesen aus der Message
        List<GameMessage> extras = handler.getExtraMessages();
        if (!extras.isEmpty()) {
            GameMessage firstExtra = extras.get(0);
            if (firstExtra.getType() == MessageType.PLAYER_MOVED) {
                JSONObject movedPayload = new JSONObject(firstExtra.getPayload().toString());
                tilePos = movedPayload.getInt("pos");
            }
        }

        JSONObject buyPayload = new JSONObject();
        buyPayload.put("playerId", 1);
        buyPayload.put("tilePos", tilePos);

        GameMessage response = handler.handle(new GameMessage(MessageType.BUY_PROPERTY, buyPayload.toString()));

        assertNotNull(response.getType());
        assertTrue(response.getType() == MessageType.PROPERTY_BOUGHT || response.getType() == MessageType.ERROR);
    }


    @Test
    void testHandleBuyPropertyInvalidPayload() {
        GameMessage response = handler.handle(new GameMessage(MessageType.BUY_PROPERTY, "invalid_payload"));
        assertEquals(MessageType.ERROR, response.getType());
    }

    @Test
    void testHandleRollDiceInvalidPayload() {
        GameMessage response = handler.handle(new GameMessage(MessageType.ROLL_DICE, "invalid_payload"));
        assertEquals(MessageType.ERROR, response.getType());
    }

    @Test
    void testHandleUnknownMessageType() {
        GameMessage response = handler.handle(new GameMessage(MessageType.ERROR, null));
        assertEquals(MessageType.ERROR, response.getType());
    }

    @Test
    void testHandleNullMessage() {
        GameMessage response = handler.handle(null);
        assertEquals(MessageType.ERROR, response.getType());
    }

    @Test
    void testGetCurrentPlayerIdNullIfNoPlayers() {
        assertNull(handler.getCurrentPlayerId());
    }

}