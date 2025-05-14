package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.board.StreetLevel;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
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
    void setup() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();

        player = new Player(1, "Alice", board);
        gameState.startGame(new ArrayList<>(List.of(player)));

        street = new StreetTile(0, "Teststraße", 200, 50, StreetLevel.NORMAL, 100, 200);
        board.getTiles().set(0, street);
        street.setOwner(player);
        street.clearBuildings();

        for (int i = 0; i < 4; i++) {
            street.buildHouse(player);
        }

        request = new BuildHotelRequest();
    }

    @Test
    void testSuccessfulHotelBuild() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", player.getId());
        payload.put("tilePos", 0);

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, street.getHotelCount());
        assertEquals(0, street.getHouseCount());
    }

    @Test
    void testInvalidPlayer() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", 999);  // nicht vorhanden
        payload.put("tilePos", 0);

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testInvalidTile() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", player.getId());
        payload.put("tilePos", 999); // existiert nicht

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTileNotOwned() {
        Player other = new Player(2, "Bob", gameState.getBoard());
        StreetTile otherStreet = new StreetTile(1, "Fremdstraße", 200, 50, StreetLevel.NORMAL, 100, 200);
        gameState.getBoard().getTiles().set(1, otherStreet);
        gameState.getPlayer(other.getId()); // damit ID gesetzt wird

        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", other.getId());
        payload.put("tilePos", 1);

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotEnoughHouses() {
        street.clearBuildings(); // keine 4 Häuser → kein Hotel möglich

        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", player.getId());
        payload.put("tilePos", 0);

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotEnoughMoney() {
        player.setCash(0); // kein Geld → kein Hotel

        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", player.getId());
        payload.put("tilePos", 0);

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testAlreadyHasHotel() {
        street.clearBuildings();
        for (int i = 0; i < 4; i++) street.buildHouse(player);
        street.buildHotel(player); // bereits Hotel gebaut

        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", player.getId());
        payload.put("tilePos", 0);

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testExceptionHandling() {
        GameMessage result = request.execute(1, "ungültig", gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }
}
