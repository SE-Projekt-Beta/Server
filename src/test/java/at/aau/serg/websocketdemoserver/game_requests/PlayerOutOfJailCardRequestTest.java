package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PlayerOutOfJailCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerOutOfJailCardRequestTest {

    private PlayerOutOfJailCardRequest request;
    private GameState gameState;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new PlayerOutOfJailCardRequest();
        gameState = mock(GameState.class);
        player = mock(Player.class);
    }

    @Test
    void testNoPlayerPresent_returnsError() {
        when(gameState.getCurrentPlayer()).thenReturn(null);

        GameMessage result = request.execute(gameState, null);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPlayerHasNoCard_returnsError() {
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.hasEscapeCard()).thenReturn(false);

        GameMessage result = request.execute(gameState, null);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPlayerUsesCard_returnsTurnContinuesMessage() {
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.hasEscapeCard()).thenReturn(true);
        when(player.getId()).thenReturn(42);

        GameMessage result = request.execute(gameState, null);

        verify(player).setEscapeCard(false);
        assertEquals(MessageType.TURN_CONTINUES, result.getType());

        PlayerOutOfJailCardPayload payload = (PlayerOutOfJailCardPayload) result.getPayload();
        assertEquals(42, payload.getPlayerId());
        assertEquals("Du bist aus dem Gefängnis entkommen.", payload.getMessage());
    }
}
