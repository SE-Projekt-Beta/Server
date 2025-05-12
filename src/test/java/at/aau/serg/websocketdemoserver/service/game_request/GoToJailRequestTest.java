package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.JailTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GoToJailRequestTest {

    private GameState gameState;
    private Player player;
    private GoToJailRequest request;
    private Tile jailTile;
    private final int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();
        player = new Player("TestPlayer", board);
        gameState.startGame(List.of(player));
        jailTile = new JailTile(31);
        request = new GoToJailRequest(jailTile);
    }

    private Map<String, Object> payloadForPlayer() {
        return Map.of("playerId", player.getId());
    }

    @Test
    void testSendToJailWithoutEscapeCard() {
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertEquals(jailTile, player.getCurrentTile());
        assertEquals(3, player.getSuspensionRounds());
        assertEquals(1, extras.size());
        assertEquals(MessageType.DRAW_RISK_CARD, extras.get(0).getType());
        assertEquals(MessageType.GAME_STATE, result.getType());
    }

    @Test
    void testAvoidJailWithEscapeCard() {
        player.setEscapeCard(true);
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertFalse(player.hasEscapeCard());
        assertNull(player.getCurrentTile()); // Nicht bewegt
        assertEquals(1, extras.size());
        assertTrue(extras.get(0).getPayload().toString().contains("Freiheitskarte"));
        assertEquals(MessageType.GAME_STATE, result.getType());
    }

    @Test
    void testInvalidPlayer() {
        List<GameMessage> extras = new ArrayList<>();
        Map<String, Object> payload = Map.of("playerId", 999); // ungültig

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testExceptionHandling() {
        List<GameMessage> extras = new ArrayList<>();
        Map<String, Object> invalid = new HashMap<>(); // kein playerId

        GameMessage result = request.execute(lobbyId, invalid, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler bei Gefängnisanweisung"));
    }
}
