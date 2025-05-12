package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BuildHouseRequestTest {

    private GameState gameState;
    private Player player;
    private StreetTile tile;
    private BuildHouseRequest request;
    private int lobbyId;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        player = new Player("Tester", gameState.getBoard());
        player.setCash(1000);
        tile = new StreetTile(5, "Murplatz", 300, 120, null, 200);
        tile.setOwner(player);
        player.setCurrentTile(tile);
        player.getOwnedStreets().add(tile);

        gameState.startGame(List.of(player));
        lobbyId = 1;
        request = new BuildHouseRequest();
    }

    private Map<String, Object> buildPayload(int playerId, int tilePos) {
        Map<String, Object> map = new HashMap<>();
        map.put("playerId", playerId);
        map.put("tilePos", tilePos);
        return map;
    }


    @Test
    void testInvalidPlayer() {
        GameMessage response = request.execute(lobbyId, buildPayload(999, tile.getIndex()), gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().name());
        assertTrue(response.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testTileNotStreet() {
        Tile fakeTile = new Tile(99) {
            @Override
            public String getLabel() {
                return "Fake";
            }

            @Override
            public at.aau.serg.websocketdemoserver.model.board.TileType getType() {
                return null;
            }
        };
        gameState.getBoard().getTiles().add(fakeTile);
        GameMessage response = request.execute(lobbyId, buildPayload(player.getId(), 99), gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().name());
        assertTrue(response.getPayload().toString().contains("Kein baubares Feld"));
    }

    @Test
    void testExceptionHandling() {
        // Passing malformed payload (e.g. missing playerId)
        Map<String, Object> badPayload = Map.of("tilePos", tile.getIndex());
        GameMessage response = request.execute(lobbyId, badPayload, gameState, new ArrayList<>());
        assertEquals("ERROR", response.getType().name());
        assertTrue(response.getPayload().toString().contains("Fehler beim Hausbau"));
    }
}

