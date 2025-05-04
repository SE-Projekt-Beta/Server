package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.MovePlayerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MovePlayerRequestTest {

    private MovePlayerRequest request;
    private GameState gameState;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new MovePlayerRequest();
        gameState = new GameState(new GameBoard());
        player = gameState.addPlayer("TestPlayer");
    }

    private GameMessage buildMessage(int playerId, int steps) {
        MovePlayerPayload payload = new MovePlayerPayload();
        payload.setPlayerId(playerId);
        payload.setDice(steps);
        return new GameMessage(MessageType.ROLL_DICE, payload);
    }

    @Test
    void testMoveToGoToJailTile() {
        player.setPosition(10); // Feld 11 = GoToJailTile
        GameMessage result = request.execute(gameState, buildMessage(player.getId(), 1));
        assertEquals(MessageType.SKIPPED_TURN, result.getType());
    }

    @Test
    void testMoveToJailTile() {
        player.setPosition(30); // Feld 31 = JailTile
        GameMessage result = request.execute(gameState, buildMessage(player.getId(), 1));
        assertEquals(MessageType.SKIPPED_TURN, result.getType());
    }

    @Test
    void testMoveToRiskTile() {
        player.setPosition(2); // Feld 3 = RiskTile
        GameMessage result = request.execute(gameState, buildMessage(player.getId(), 1));
        assertEquals(MessageType.DRAW_EVENT_CARD, result.getType());
    }

    @Test
    void testMoveToTaxTile() {
        player.setPosition(20); // Feld 21 = TaxTile
        GameMessage result = request.execute(gameState, buildMessage(player.getId(), 1));
        assertEquals(MessageType.PAY_TAX, result.getType());
    }

    @Test
    void testMoveToUnownedStreetTile() {
        player.setPosition(1); // Feld 2 = Amtsplatz, unbesessen
        GameMessage result = request.execute(gameState, buildMessage(player.getId(), 1));
        assertEquals(MessageType.CAN_BUY_PROPERTY, result.getType());
    }

    @Test
    void testMoveToOwnedStreetTile() {
        // Setup: anderer Spieler besitzt Amtsplatz (Feld 2)
        Player owner = gameState.addPlayer("Owner");
        assertTrue(owner.purchaseStreet(2));

        // Testspieler landet auf Feld 2
        player.setPosition(1); // davor
        GameMessage result = request.execute(gameState, buildMessage(player.getId(), 1));
        assertEquals(MessageType.PAY_RENT, result.getType());
    }

    @Test
    void testMoveToNeutralTile() {
        player.setPosition(5); // Feld 6 = Schloßallee, unbesessen
        GameMessage result = request.execute(gameState, buildMessage(player.getId(), 1));

        // Schloßallee ist ein StreetTile, also CAN_BUY_PROPERTY
        assertEquals(MessageType.CAN_BUY_PROPERTY, result.getType());
    }

    @Test
    void testInvalidPlayer() {
        GameMessage result = request.execute(gameState, buildMessage(-1, 1));
        assertEquals(MessageType.ERROR, result.getType());
    }
}
