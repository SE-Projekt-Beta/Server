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

class BuildHouseRequestTest {

    private GameState gameState;
    private Player player;
    private StreetTile street;
    private BuildHouseRequest request;

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

        request = new BuildHouseRequest();
    }

    @Test
    void testSuccessfulHouseBuild() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", player.getId());
        payload.put("tilePos", 0);

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, street.getHouseCount());
    }

    @Test
    void testInvalidPlayer() {
        Map<String, Object> payload = Map.of(
                "playerId", 999,
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testInvalidTile() {
        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 999
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTileNotOwned() {
        Player other = new Player(2, "Bob", gameState.getBoard());
        StreetTile otherTile = new StreetTile(1, "Fremdstraße", 200, 50, StreetLevel.NORMAL, 100, 200);
        gameState.getBoard().getTiles().set(1, otherTile);

        Map<String, Object> payload = Map.of(
                "playerId", other.getId(),
                "tilePos", 1
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTooManyHouses() {
        for (int i = 0; i < 4; i++) {
            street.buildHouse(player);
        }

        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotEnoughMoney() {
        player.setCash(0);

        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testAlreadyHasHotel() {
        for (int i = 0; i < 4; i++) {
            street.buildHouse(player);
        }
        street.buildHotel(player);

        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testMalformedPayload() {
        GameMessage result = request.execute(1, "ungültig", gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }
}
