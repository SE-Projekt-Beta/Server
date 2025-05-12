package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PassedStartRequestTest {

    private GameState gameState;
    private Player player;
    private PassedStartRequest request;
    private final int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();
        player = new Player("StartPlayer", board);
        gameState.startGame(List.of(player));
        request = new PassedStartRequest();
    }

    private Map<String, Object> payload() {
        return Map.of("playerId", player.getId());
    }

    @Test
    void testPassedStart_DefaultBonus() {
        int oldCash = player.getCash();
        player.setCurrentTile(new SpecialTile(5, "Random", TileType.BANK));

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload(), gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, extras.size());
        assertEquals(MessageType.CASH_TASK, extras.get(0).getType());
        assertEquals(oldCash + 200, player.getCash());
    }

    @Test
    void testExactStartPosition_ExactBonus() {
        int oldCash = player.getCash();
        player.setCurrentTile(new SpecialTile(1, "Start", TileType.START));

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload(), gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, extras.size());
        assertEquals(oldCash + 400, player.getCash());
    }

    @Test
    void testInvalidPlayer() {
        Map<String, Object> invalidPayload = Map.of("playerId", 999);
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, invalidPayload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testExceptionHandling() {
        Map<String, Object> invalidPayload = new HashMap<>(); // kein playerId
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, invalidPayload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Startbonus"));
    }
}
