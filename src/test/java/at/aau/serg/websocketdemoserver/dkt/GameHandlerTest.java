package at.aau.serg.websocketdemoserver.dkt;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;

public class GameHandlerTest {
    @Test
    void testHandleRollDiceReturnsPlayerMoved() {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\": \"player1\"}";
        GameMessage input = new GameMessage("roll_dice", payload);

        GameMessage result = handler.handle(input);

        assertNotNull(result);
        assertEquals("player_moved", result.getType());
        assertTrue(result.getPayload().contains("player1"));
        assertTrue(result.getPayload().contains("pos"));
    }

    @Test
    void testPlayerMovedIncludesTileData() throws JSONException {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"player1\"}";

        GameMessage result = handler.handle(new GameMessage("roll_dice", payload));

        assertEquals("player_moved", result.getType());

        JSONObject obj = new JSONObject(result.getPayload());
        assertTrue(obj.has("tileName"), "tileName fehlt im Payload");
        assertTrue(obj.has("tileType"), "tileType fehlt im Payload");
    }

    @Test
    void testTileTypeIsValid() throws JSONException {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"test\"}";
        GameMessage result = handler.handle(new GameMessage("roll_dice", payload));

        JSONObject obj = new JSONObject(result.getPayload());
        String type = obj.getString("tileType");

        // Anpassen der erlaubten Typen
        assertTrue(
                type.matches("start|street|station|event|tax|jail|goto_jail|free"),
                "tileType ist kein gültiger Typ: " + type
        );
    }

    @Test
    void testPlayerPositionIsStored() {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"p1\"}";
        handler.handle(new GameMessage("roll_dice", payload));

        int pos = handler.getGameState().getPosition("p1");
        assertTrue(pos >= 0 && pos < 40, "Position liegt nicht im gültigen Bereich");
    }








}
