package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.StreetLevel;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BuildHotelRequestTest {

    private GameState gameState;
    private Player player;
    private StreetTile street;
    private BuildHotelRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        player = new Player("Tester", gameState.getBoard());
        gameState.startGame(List.of(player));
        street = new StreetTile(10, "Hotelstraße", 200, 50, StreetLevel.NORMAL, 100);
        gameState.getBoard().getTiles().add(street);
        street.setOwner(player);
        player.getOwnedStreets().add(street);
        request = new BuildHotelRequest();
    }

    private Map<String, Object> buildPayload(int playerId, int tilePos) {
        Map<String, Object> map = new HashMap<>();
        map.put("playerId", playerId);
        map.put("tilePos", tilePos);
        return map;
    }


    @Test
    void testPlayerNotFound() {
        Map<String, Object> payload = buildPayload(999, street.getIndex());
        GameMessage response = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().toString());
    }

    @Test
    void testPlayerDead() {
        player.eliminate();
        Map<String, Object> payload = buildPayload(player.getId(), street.getIndex());
        GameMessage response = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().toString());
    }

    @Test
    void testInvalidTile() {
        Map<String, Object> payload = buildPayload(player.getId(), 999);
        GameMessage response = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().toString());
    }

    @Test
    void testNotOwner() {
        Player other = new Player("Fremd", gameState.getBoard());
        gameState.getBoard().getTiles().add(new StreetTile(12, "Fremdstraße", 200, 50, StreetLevel.NORMAL, 100));
        Tile foreign = gameState.getBoard().getTile(12);
        Map<String, Object> payload = buildPayload(other.getId(), foreign.getIndex());
        GameMessage response = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().toString());
    }

    @Test
    void testBuildHotelNotPossible() {
        // Keine 4 Häuser gebaut
        street.buildHouse(player); // nur 1 Haus
        Map<String, Object> payload = buildPayload(player.getId(), street.getIndex());
        GameMessage response = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().toString());
    }

    @Test
    void testMalformedPayload() {
        // Kein playerId oder tilePos
        Map<String, Object> badPayload = Map.of("x", 123);
        GameMessage response = request.execute(1, badPayload, gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().toString());
        assertTrue(response.getPayload().toString().contains("Fehler beim Hotelbau"));
    }
}

