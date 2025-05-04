package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.RollDiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RollDiceRequestTest {

    private RollDiceRequest handler;
    private GameState gameState;
    private Player player;

    @BeforeEach
    void setUp() {
        handler = new RollDiceRequest();
        gameState = new GameState(new GameBoard());
        player = gameState.addPlayer("Alice");
    }

    @Test
    void testPlayerNotFound() {
        RollDicePayload payload = new RollDicePayload();
        payload.setPlayerId(999); // Nicht existierender Spieler
        GameMessage msg = new GameMessage(MessageType.ROLL_DICE, payload);
        GameMessage result = handler.execute(gameState, msg);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotYourTurn() {
        Player other = new Player("Bob", gameState.getBoard());
        gameState.addPlayer(other);

        RollDicePayload payload = new RollDicePayload();
        payload.setPlayerId(other.getId()); // Nicht aktueller Spieler

        GameMessage msg = new GameMessage(MessageType.ROLL_DICE, payload);
        GameMessage result = handler.execute(gameState, msg);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Nicht dein Zug"));
    }

    @Test
    void testSuspendedPlayer() {
        player.suspendForRounds(2);
        SpecialTile dummyTile = new SpecialTile(10, "Dummy", null);
        player.setCurrentTile(dummyTile);

        RollDicePayload payload = new RollDicePayload();
        payload.setPlayerId(player.getId());

        GameMessage msg = new GameMessage(MessageType.ROLL_DICE, payload);
        GameMessage result = handler.execute(gameState, msg);

        assertEquals(MessageType.SKIPPED_TURN, result.getType());
        SkippedTurnPayload skipped = result.parsePayload(SkippedTurnPayload.class);
        assertEquals(player.getId(), skipped.getPlayerId());
        assertEquals(10, skipped.getTilePos());
        assertEquals("Dummy", skipped.getTileName());
        assertEquals(2, skipped.getSuspension());
    }

    @Test
    void testValidRoll() {
        SpecialTile startTile = new SpecialTile(0, "Start", null);
        player.setCurrentTile(startTile);

        RollDicePayload payload = new RollDicePayload();
        payload.setPlayerId(player.getId());

        GameMessage msg = new GameMessage(MessageType.ROLL_DICE, payload);
        GameMessage result = handler.execute(gameState, msg);

        assertEquals(MessageType.DICE_ROLLED, result.getType());

        RollDiceResultPayload resultPayload = result.parsePayload(RollDiceResultPayload.class);
        assertNotNull(resultPayload.getMove());
        assertNotNull(resultPayload.getNext());

        assertEquals(player.getId(), resultPayload.getMove().getPlayerId());
        assertTrue(resultPayload.getMove().getDice() >= 1 && resultPayload.getMove().getDice() <= 6);
    }
}
