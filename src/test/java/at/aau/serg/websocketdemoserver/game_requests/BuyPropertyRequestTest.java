package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.BuyPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.BuyPropertyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BuyPropertyRequestTest {

    private BuyPropertyRequest request;
    private GameState gameState;
    private GameMessage message;
    private BuyPropertyPayload payload;
    private Player player;
    private StreetTile streetTile;

    @BeforeEach
    void setUp() {
        request = new BuyPropertyRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(BuyPropertyPayload.class);
        player = mock(Player.class);
        streetTile = mock(StreetTile.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(BuyPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testWrongPlayerTurn_returnsError() {
        when(message.parsePayload(BuyPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(player);
        Player other = mock(Player.class);
        when(other.getId()).thenReturn(999);
        when(gameState.getCurrentPlayer()).thenReturn(other);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

}
