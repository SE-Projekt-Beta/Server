package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.CashTaskPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLostPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.CashTaskHandlingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CashTaskHandlingRequestTest {

    private GameState gameState;
    private CashTaskHandlingRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new CashTaskHandlingRequest();
    }

    @Test
    void testPlayerNotFound() {
        CashTaskPayload payload = new CashTaskPayload(999, -100, 0);
        GameMessage message = new GameMessage(MessageType.CASH_TASK, payload);
        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }

    @Test
    void testPlayerGoesBankrupt() {
        Player player = gameState.addPlayer("BankrottTester");
        player.setCash(10);

        CashTaskPayload payload = new CashTaskPayload(player.getId(), -100, -90);
        GameMessage message = new GameMessage(MessageType.CASH_TASK, payload);
        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.PLAYER_LOST, result.getType());
        PlayerLostPayload lostPayload = result.parsePayload(PlayerLostPayload.class);
        assertEquals(player.getId(), lostPayload.getPlayerId());
        assertEquals(player.getNickname(), lostPayload.getNickname());
    }

    @Test
    void testCashUpdateSuccess() {
        Player player = gameState.addPlayer("Geldspieler");
        player.setCash(100);

        CashTaskPayload payload = new CashTaskPayload(player.getId(), 50, 150);
        GameMessage message = new GameMessage(MessageType.CASH_TASK, payload);
        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.CASH_TASK, result.getType());
        CashTaskPayload resultPayload = result.parsePayload(CashTaskPayload.class);
        assertEquals(player.getId(), resultPayload.getPlayerId());
        assertEquals(150, resultPayload.getNewCash());
        assertEquals(150, player.getCash());
    }
}