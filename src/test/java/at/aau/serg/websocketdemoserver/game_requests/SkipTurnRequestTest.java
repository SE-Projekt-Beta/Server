package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.SkippedTurnPayload;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SkipTurnRequestTest {

    private GameState gameState;
    private Player player;
    private SkipTurnRequest handler;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        player = gameState.addPlayer("Alice");
        handler = new SkipTurnRequest();
    }

    @Test
    void testPlayerNotFound() {
        SkippedTurnPayload payload = new SkippedTurnPayload();
        payload.setPlayerId(999); // nicht existierender Spieler

        GameMessage message = new GameMessage(MessageType.SKIPPED_TURN, payload);
        GameMessage result = handler.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPlayerNotSuspended() {
        SkippedTurnPayload payload = new SkippedTurnPayload();
        payload.setPlayerId(player.getId()); // Spieler existiert, aber nicht gesperrt

        GameMessage message = new GameMessage(MessageType.SKIPPED_TURN, payload);
        GameMessage result = handler.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testValidSkipTurn() {
        player.suspendForRounds(2);
        player.setCurrentTile(new SpecialTile(8, "Dummy", null));

        SkippedTurnPayload payload = new SkippedTurnPayload();
        payload.setPlayerId(player.getId());

        GameMessage message = new GameMessage(MessageType.SKIPPED_TURN, payload);
        GameMessage result = handler.execute(gameState, message);

        assertEquals(MessageType.SKIPPED_TURN, result.getType());

        SkippedTurnPayload response = result.parsePayload(SkippedTurnPayload.class);
        assertEquals(player.getId(), response.getPlayerId());
        assertEquals(8, response.getTilePos());
        assertEquals("Dummy", response.getTileName());
        assertEquals(1, response.getSuspension()); // vorher 2, danach 1
    }
}
