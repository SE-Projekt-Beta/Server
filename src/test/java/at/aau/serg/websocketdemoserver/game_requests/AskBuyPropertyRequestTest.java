package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.AskBuyPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.AskBuyPropertyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AskBuyPropertyRequestTest {

    private AskBuyPropertyRequest request;
    private GameState gameState;
    private GameMessage message;
    private AskBuyPropertyPayload payload;
    private Player player;
    private StreetTile streetTile;

    @BeforeEach
    void setUp() {
        request = new AskBuyPropertyRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(AskBuyPropertyPayload.class);
        player = mock(Player.class);
        streetTile = mock(StreetTile.class);
    }


    @Test
    void testPlayerNotFound_returnsError() {
        when(gameState.isGameStarted()).thenReturn(true);
        when(message.parsePayload(AskBuyPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }


    @Test
    void testTileNotStreet_returnsError() {
        when(gameState.isGameStarted()).thenReturn(true);
        when(message.parsePayload(AskBuyPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(3);
        when(gameState.getPlayer(3)).thenReturn(player);
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(mock(at.aau.serg.websocketdemoserver.model.board.Tile.class)); // Not a StreetTile

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testStreetAlreadyOwned_returnsError() {
        when(gameState.isGameStarted()).thenReturn(true);
        when(message.parsePayload(AskBuyPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(4);
        when(gameState.getPlayer(4)).thenReturn(player);
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(streetTile);
        when(streetTile.getOwner()).thenReturn(mock(Player.class));

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testCanBuyProperty_success() {
        when(gameState.isGameStarted()).thenReturn(true);
        when(message.parsePayload(AskBuyPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(5);
        when(gameState.getPlayer(5)).thenReturn(player);
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(streetTile);
        when(streetTile.getOwner()).thenReturn(null);
        when(streetTile.getIndex()).thenReturn(10);
        when(streetTile.getLabel()).thenReturn("Hauptstraße");
        when(streetTile.getPrice()).thenReturn(300);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.CAN_BUY_PROPERTY, result.getType());
        AskBuyPropertyPayload response = (AskBuyPropertyPayload) result.getPayload();
        assertEquals(5, response.getPlayerId());
        assertEquals(10, response.getTilePos());
        assertEquals("Hauptstraße", response.getTileName());
        assertEquals(300, response.getPrice());

    }
}
