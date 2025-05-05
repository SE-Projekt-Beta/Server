package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.CashTaskPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.CashTaskHandlingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CashTaskHandlingRequestTest {

    private CashTaskHandlingRequest request;
    private GameState gameState;
    private GameMessage message;
    private CashTaskPayload payload;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new CashTaskHandlingRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(CashTaskPayload.class);
        player = mock(Player.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(CashTaskPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNegativeAmount_withBankruptcy_returnsBankruptMessage() {
        when(message.parsePayload(CashTaskPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(payload.getAmount()).thenReturn(-100);
        when(gameState.getPlayer(1)).thenReturn(player);

        GameMessage bankrupt = mock(GameMessage.class);
        when(player.deductCash(100)).thenReturn(bankrupt);

        GameMessage result = request.execute(gameState, message);
        assertEquals(bankrupt, result);
    }

    @Test
    void testNegativeAmount_noBankruptcy_returnsCashTask() {
        when(message.parsePayload(CashTaskPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(2);
        when(payload.getAmount()).thenReturn(-50);
        when(gameState.getPlayer(2)).thenReturn(player);
        when(player.deductCash(50)).thenReturn(null);
        when(player.getCash()).thenReturn(150);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.CASH_TASK, result.getType());
        CashTaskPayload response = (CashTaskPayload) result.getPayload();
        assertEquals(2, response.getPlayerId());
        assertEquals(-50, response.getAmount());
        assertEquals(150, response.getNewCash());
    }

    @Test
    void testPositiveAmount_setsCashCorrectly_returnsCashTask() {
        when(message.parsePayload(CashTaskPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(3);
        when(payload.getAmount()).thenReturn(200);
        when(gameState.getPlayer(3)).thenReturn(player);
        when(player.getCash()).thenReturn(300).thenReturn(500); // Before + After set

        GameMessage result = request.execute(gameState, message);

        verify(player).setCash(500); // 300 + 200
        assertEquals(MessageType.CASH_TASK, result.getType());
        CashTaskPayload response = (CashTaskPayload) result.getPayload();
        assertEquals(3, response.getPlayerId());
        assertEquals(200, response.getAmount());
        assertEquals(500, response.getNewCash());
    }
}
