package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.DrawEventCardPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.BankTile;
import at.aau.serg.websocketdemoserver.model.board.RiskTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import at.aau.serg.websocketdemoserver.model.cards.RiskCardDeck;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.DrawEventCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DrawEventCardRequestTest {

    private DrawEventCardRequest request;
    private GameState gameState;
    private GameMessage message;
    private DrawEventCardPayload payload;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new DrawEventCardRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(DrawEventCardPayload.class);
        player = mock(Player.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(DrawEventCardPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);
        assertEquals("ERROR", result.getType().name());
    }

    @Test
    void testNotOnEventTile_returnsError() {
        when(message.parsePayload(DrawEventCardPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(2);
        when(gameState.getPlayer(2)).thenReturn(player);
        Tile normalTile = mock(Tile.class);
        when(player.getCurrentTile()).thenReturn(normalTile);

        GameMessage result = request.execute(gameState, message);
        assertEquals("ERROR", result.getType().name());
    }
}
