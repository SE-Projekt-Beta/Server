package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.StreetTileFactory;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PayRentRequestTest {

    private GameState gameState;
    private Player player;
    private Player owner;
    private StreetTile street;
    private PayRentRequest request;
    private final int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();

        player = new Player("Mieter", board);
        owner = new Player("Vermieter", board);

        gameState.startGame(List.of(player, owner));

        street = StreetTileFactory.createStreetTile(5); // gültiges Feld
        assertNotNull(street);
        street.setOwner(owner);
        player.setCurrentTile(street);

        request = new PayRentRequest();
    }

    private Map<String, Object> validPayload() {
        return Map.of("playerId", player.getId(), "tilePos", street.getIndex());
    }

    @Test
    void testPayRentSuccess() {
        int initialCash = player.getCash();
        int ownerCash = owner.getCash();
        int rent = street.calculateRent();

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, validPayload(), gameState, extras);

        assertEquals(initialCash - rent, player.getCash());
        assertEquals(ownerCash + rent, owner.getCash());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertTrue(extras.isEmpty());
    }

    @Test
    void testPlayerGoesBankrupt() {
        player.setCash(10); // Weniger als mögliche Miete
        int rent = street.calculateRent();

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, validPayload(), gameState, extras);

        assertFalse(player.isAlive());
        assertEquals(MessageType.PLAYER_LOST, extras.get(0).getType());
        assertEquals(MessageType.GAME_STATE, result.getType());
    }

    @Test
    void testOwnerIsSameAsPlayer() {
        street.setOwner(player); // selbst Eigentümer

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, validPayload(), gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Keine Miete zu zahlen"));
    }

    @Test
    void testTileIsNotStreet() {
        Map<String, Object> invalid = Map.of("playerId", player.getId(), "tilePos", 1); // Startfeld

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, invalid, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Kein gültiges Mietfeld"));
    }

    @Test
    void testPlayerDoesNotExist() {
        Map<String, Object> invalid = Map.of("playerId", 999, "tilePos", street.getIndex());

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, invalid, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testExceptionHandling() {
        Map<String, Object> brokenPayload = new HashMap<>(); // fehlt playerId und tilePos

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, brokenPayload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Miete zahlen"));
    }
}
