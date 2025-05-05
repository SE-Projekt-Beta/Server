package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.RollDiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RollDiceRequestTest {

    private RollDiceRequest request;
    private GameState gameState;
    private GameMessage message;
    private RollDicePayload payload;
    private Player player;
    private Player nextPlayer;
    private Tile tile;

    @BeforeEach
    void setUp() {
        request = new RollDiceRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(RollDicePayload.class);
        player = mock(Player.class);
        nextPlayer = mock(Player.class);
        tile = mock(Tile.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(RollDicePayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testWrongPlayerTurn_returnsError() {
        when(message.parsePayload(RollDicePayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(player);

        Player otherPlayer = mock(Player.class);
        when(otherPlayer.getId()).thenReturn(2);
        when(gameState.getCurrentPlayer()).thenReturn(otherPlayer);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }
}
