package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.StartMoneyPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.StartMoneyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartMoneyRequestTest {

    private StartMoneyRequest request;
    private GameState gameState;
    private GameMessage message;
    private StartMoneyPayload payload;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new StartMoneyRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(StartMoneyPayload.class);
        player = mock(Player.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(StartMoneyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testStartMoneyGranted_correctBonusCalculation() {
        when(message.parsePayload(StartMoneyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(2);
        when(payload.getBonusAmount()).thenReturn(2); // Faktor
        when(gameState.getPlayer(2)).thenReturn(player);
        when(player.getCash()).thenReturn(500); // vorheriges Geld

        GameMessage result = request.execute(gameState, message);

        // 200 * 2 = 400 → 500 + 400 = 900
        verify(player).setCash(900);

        assertEquals(MessageType.START_MONEY, result.getType());
        StartMoneyPayload resultPayload = (StartMoneyPayload) result.getPayload();
        assertEquals(2, resultPayload.getPlayerId());
        assertEquals(400, resultPayload.getBonusAmount());
        assertEquals(900, resultPayload.getNewCash());
    }
}
