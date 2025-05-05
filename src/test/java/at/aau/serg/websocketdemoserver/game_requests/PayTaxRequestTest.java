package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PayTaxPayload;
import at.aau.serg.websocketdemoserver.dto.PlayerLostPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PayTaxRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayTaxRequestTest {

    private PayTaxRequest request;
    private GameState gameState;
    private GameMessage message;
    private PayTaxPayload payload;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new PayTaxRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(PayTaxPayload.class);
        player = mock(Player.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(PayTaxPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTaxPaidSuccessfully_returnsTaxPaidMessage() {
        when(message.parsePayload(PayTaxPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(2);
        when(payload.getAmount()).thenReturn(100);
        when(payload.getTileName()).thenReturn("Einkommensteuer");
        when(gameState.getPlayer(2)).thenReturn(player);
        when(player.getId()).thenReturn(2);
        when(player.getCash()).thenReturn(500).thenReturn(400); // oldCash, newCash
        when(player.deductCash(100)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.TAX_PAID, result.getType());
        PayTaxPayload response = (PayTaxPayload) result.getPayload();
        assertEquals(2, response.getPlayerId());
        assertEquals("Einkommensteuer", response.getTileName());
        assertEquals(100, response.getAmount());
        assertEquals(500, response.getOldCash());
        assertEquals(400, response.getNewCash());
    }

    @Test
    void testBankrupt_setsGameOverCorrectly_returnsPlayerLost() {
        GameMessage bankruptMessage = new GameMessage(MessageType.PLAYER_LOST, new PlayerLostPayload());
        PlayerLostPayload lostPayload = (PlayerLostPayload) bankruptMessage.getPayload();

        when(message.parsePayload(PayTaxPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(3);
        when(payload.getAmount()).thenReturn(300);
        when(payload.getTileName()).thenReturn("Luxussteuer");
        when(gameState.getPlayer(3)).thenReturn(player);
        when(player.getId()).thenReturn(3);
        when(player.getCash()).thenReturn(150).thenReturn(0); // before & after
        when(player.deductCash(300)).thenReturn(bankruptMessage);
        when(gameState.getPlayers()).thenReturn(List.of(mock(Player.class))); // nur 1 übrig

        GameMessage result = request.execute(gameState, message);

        verify(gameState).removePlayer(player);
        assertEquals(MessageType.PLAYER_LOST, result.getType());
        assertTrue(lostPayload.isGameOver());
    }
}
