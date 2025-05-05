package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.JailCardHandlingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JailCardHandlingRequestTest {

    private JailCardHandlingRequest request;
    private GameState gameState;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new JailCardHandlingRequest();
        gameState = mock(GameState.class);
        player = mock(Player.class);
    }

    @Test
    void testPlayerHasNoEscapeCard_returnsError() {
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(player);
        when(player.hasEscapeCard()).thenReturn(false);

        GameMessage result = request.execute(gameState, null);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPlayerUsesEscapeCard_returnsSuccessMessage() {
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getId()).thenReturn(2);
        when(player.getNickname()).thenReturn("Eva");
        when(gameState.getPlayer(2)).thenReturn(player);
        when(player.hasEscapeCard()).thenReturn(true);

        GameMessage result = request.execute(gameState, null);

        verify(player).setEscapeCard(false);
        verify(player).resetSuspension();

        assertEquals(MessageType.PLAYER_OUT_OF_JAIL_CARD, result.getType());

        PlayerOutOfJailCardPayload payload = (PlayerOutOfJailCardPayload) result.getPayload();
        assertEquals(2, payload.getPlayerId());
        assertEquals("Eva", payload.getMessage()
        );
    }
}
