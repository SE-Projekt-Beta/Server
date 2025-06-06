package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PayTaxRequestTest {

    private GameState gameState;
    private Player player;
    private PayTaxRequest request;
    private final int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();
        player = new Player("Spieler", board);
        gameState.startGame(List.of(player));
        request = new PayTaxRequest();
    }

    private Map<String, Object> createPayload() {
        return Map.of("playerId", player.getId());
    }

    private SpecialTile createTaxTile(int index, String label) {
        return new SpecialTile(index, label, TileType.TAX);
    }

    @Test
    void testPaySondersteuer() {
        Tile taxTile = gameState.getBoard().getTile(21);
        player.setCurrentTile(taxTile);
        player.setCash(500);

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, createPayload(), gameState, extras);

        assertEquals(300, player.getCash());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, extras.size());
        assertEquals(MessageType.PAY_TAX, extras.get(0).getType());
    }

    @Test
    void testPayVermoegensabgabe() {
        Tile taxTile = gameState.getBoard().getTile(33);
        player.setCurrentTile(taxTile);
        player.setCash(1000);

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, createPayload(), gameState, extras);

        assertEquals(600, player.getCash());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, extras.size());
        assertEquals(MessageType.PAY_TAX, extras.get(0).getType());
    }

    @Test
    void testBankruptcyAfterTax() {
        SpecialTile tile = createTaxTile(33, "Vermögensabgabe");
        player.setCurrentTile(tile);
        player.setCash(200);

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, createPayload(), gameState, extras);

        assertFalse(player.isAlive());
        assertEquals(MessageType.PLAYER_LOST, extras.get(0).getType());
        assertEquals(MessageType.GAME_STATE, result.getType());
    }

    @Test
    void testInvalidTileType() {
        // Setze Spieler auf ein Feld, das kein Steuerfeld ist
        player.setCurrentTile(new SpecialTile(1, "Start", TileType.START));

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, createPayload(), gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Kein Steuerfeld"));
    }

    @Test
    void testUnknownTaxTile() {
        SpecialTile unknown = createTaxTile(99, "Unbekannt");
        player.setCurrentTile(unknown);

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, createPayload(), gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Unbekanntes Steuerfeld"));
    }

    @Test
    void testPlayerNotFound() {
        Map<String, Object> payload = Map.of("playerId", 999);
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }

    @Test
    void testExceptionHandling() {
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, Map.of(), gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Steuerzahlen"));
    }
}
