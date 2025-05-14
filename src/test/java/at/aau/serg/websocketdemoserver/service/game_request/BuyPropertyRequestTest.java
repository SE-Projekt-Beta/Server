package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.board.StreetLevel;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BuyPropertyRequestTest {

    private GameState gameState;
    private Player player;
    private StreetTile street;
    private BuyPropertyRequest request;

    @BeforeEach
    void setup() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();

        player = new Player(1, "Alice", board);
        gameState.startGame(new ArrayList<>(List.of(player)));

        street = new StreetTile(0, "Hauptstraße", 300, 50, StreetLevel.NORMAL, 100, 200);
        board.getTiles().set(0, street);

        request = new BuyPropertyRequest();
    }

    @Test
    void testSuccessfulPurchase() {
        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(player, street.getOwner());
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
    void testTileNotBuyable() {
        // Ersetze das Feld durch ein nicht-kaufbares Tile
        gameState.getBoard().getTiles().set(0, new Tile(0) {
            @Override
            public TileType getType() {
                return TileType.START;
            }
        });

        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testAlreadyOwnedProperty() {
        street.setOwner(player);

        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPurchaseFailsDueToLackOfMoney() {
        player.setCash(0);

        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0
        );

        GameMessage result = request.execute(1, payload, gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }


    @Test
    void testInvalidPayload() {
        GameMessage result = request.execute(1, "blabla", gameState, new ArrayList<>());
        assertEquals(MessageType.ERROR, result.getType());
    }
}
