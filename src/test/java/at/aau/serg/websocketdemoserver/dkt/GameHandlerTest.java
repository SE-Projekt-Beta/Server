package at.aau.serg.websocketdemoserver.dkt;

import at.aau.serg.websocketdemoserver.dkt.tiles.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;

import java.util.List;

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
        String type = obj.getString("tileType").toLowerCase();

        // Anpassen der erlaubten Typen
        assertTrue(
                type.matches("start|street|station|event|event_risiko|event_bank|tax|jail|goto_jail|free"),
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

    @Test
    void testMultipleRollsNoCrash() {
        GameHandler handler = new GameHandler();

        for (int i = 0; i < 10; i++) {
            String payload = "{\"playerId\":\"multi\"}";
            GameMessage result = handler.handle(new GameMessage("roll_dice", payload));
            assertEquals("player_moved", result.getType());
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
        assertEquals("can_buy_property", msg.getType());
    }

    @Test
    void testDecideActionForTax() {
        GameHandler handler = new GameHandler();
        Tax tile = new Tax(4, "Einkommenssteuer", 100);

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals("pay_tax", msg.getType());
    }

    @Test
    void testDecideActionForRisikoEvent() {
        GameHandler handler = new GameHandler();
        RisikoEvent tile = new RisikoEvent(2, "event_risiko");

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals("event_card_risiko", msg.getType());
    }

    @Test
    void testDecideActionForBankEvent() {
        GameHandler handler = new GameHandler();
        BankEvent tile = new BankEvent(2, "event_bank");

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals("event_card_bank", msg.getType());
    }

    @Test
    void testDecideActionForGoToJailEvent() {
        GameHandler handler = new GameHandler();
        String playerId = "test_player";

        Tile goToJailTile = new GoToJail(30, "Gehe ins Gefängnis");
        GameMessage msg = handler.decideAction(playerId, goToJailTile);

        assertEquals("go_to_jail", msg.getType());

        int newPos = handler.getGameState().getPosition(playerId);
        assertEquals(10, newPos);
        assertTrue(handler.getGameState().isInJail(playerId));
    }

    @Test
    void testDecideActionForFreeField() {
        GameHandler handler = new GameHandler();
        Free tile = new Free(20, "Frei Parken");

        GameMessage msg = handler.decideAction("player1", tile);
        assertEquals("skipped", msg.getType());
    }

    @Test
    void testExtraActionGeneratedAfterRoll() {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerId\":\"player1\"}";
        handler.handle(new GameMessage("roll_dice", payload));

        List<GameMessage> extras = handler.getExtraMessages();
        assertEquals(1, extras.size(), "Es sollte genau eine Aktionsnachricht geben");

        GameMessage action = extras.get(0);
        assertNotNull(action.getType(), "Aktionstyp darf nicht null sein");
        assertTrue(action.getType().matches("can_buy_property|pay_tax|event_risiko|event_bank|go_to_jail|skipped"),

                "Unerwarteter Aktionstyp: " + action.getType());
    }

    @Test
    void testBuyPropertyStoresOwnership() throws JSONException {
        GameHandler handler = new GameHandler();

        // Spieler "player1" kauft Feld Nr. 5
        JSONObject payload = new JSONObject();
        payload.put("playerId", "player1");
        payload.put("tilePos", 5);

        GameMessage result = handler.handle(new GameMessage("buy_property", payload.toString()));

        assertEquals("property_bought", result.getType());
        assertEquals("player1", handler.getOwner(5), "Feld 5 sollte nun player1 gehören");
    }

    @Test
    void testHandleJoinLobby() throws JSONException {
        GameHandler handler = new GameHandler();
        String payload = "{\"playerName\":\"player1\"}";

        GameMessage result = handler.handle(new GameMessage("join_lobby", payload));

        assertNotNull(result);
        assertEquals("lobby_update", result.getType());

        JSONObject obj = new JSONObject(result.getPayload());
        JSONArray players = obj.getJSONArray("players");

        assertEquals("Player1", players.getString(0), "Lobby sollte player1 enthalten");
    }

}
