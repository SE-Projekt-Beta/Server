package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PayRentPayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PayRentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayRentRequestTest {

    private PayRentRequest request;
    private GameState gameState;
    private Player currentPlayer;
    private GameMessage bankruptMessage;

    @BeforeEach
    void setUp() {
        request = new PayRentRequest();
        gameState = mock(GameState.class);
        currentPlayer = mock(Player.class);
        bankruptMessage = mock(GameMessage.class);
    }

    @Test
    void testTileNotStreet_returnsError() {
        Tile tile = mock(Tile.class);

        when(gameState.getCurrentPlayer()).thenReturn(currentPlayer);
        when(currentPlayer.getCurrentTile()).thenReturn(tile);

        GameMessage result = request.execute(gameState, null);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNoOwnerOrOwnField_returnsError() {
        StreetTile street = mock(StreetTile.class);

        when(gameState.getCurrentPlayer()).thenReturn(currentPlayer);
        when(currentPlayer.getCurrentTile()).thenReturn(street);
        when(currentPlayer.getId()).thenReturn(1);
        when(street.getOwner()).thenReturn(null);  // no owner

        GameMessage result1 = request.execute(gameState, null);
        assertEquals(MessageType.ERROR, result1.getType());

        // simulate own field
        when(street.getOwner()).thenReturn(currentPlayer);
        GameMessage result2 = request.execute(gameState, null);
        assertEquals(MessageType.ERROR, result2.getType());
    }

    @Test
    void testBankruptcyDuringRent_returnsBankruptMessage() {
        StreetTile street = mock(StreetTile.class);
        Player owner = mock(Player.class);

        when(gameState.getCurrentPlayer()).thenReturn(currentPlayer);
        when(currentPlayer.getCurrentTile()).thenReturn(street);
        when(currentPlayer.getId()).thenReturn(1);
        when(owner.getId()).thenReturn(2);
        when(street.getOwner()).thenReturn(owner);
        when(street.calculateRent()).thenReturn(150);
        when(currentPlayer.transferCash(owner, 150)).thenReturn(bankruptMessage);

        GameMessage result = request.execute(gameState, null);
        assertEquals(bankruptMessage, result);
    }

    @Test
    void testRentPaid_returnsRentPaidMessage() {
        StreetTile street = mock(StreetTile.class);
        Player owner = mock(Player.class);

        when(gameState.getCurrentPlayer()).thenReturn(currentPlayer);
        when(currentPlayer.getCurrentTile()).thenReturn(street);
        when(currentPlayer.getId()).thenReturn(1);
        when(owner.getId()).thenReturn(2);
        when(street.getOwner()).thenReturn(owner);
        when(street.calculateRent()).thenReturn(200);
        when(currentPlayer.transferCash(owner, 200)).thenReturn(null);

        GameMessage result = request.execute(gameState, null);

        assertEquals(MessageType.RENT_PAID, result.getType());
        PayRentPayload payload = (PayRentPayload) result.getPayload();
        assertEquals(1, payload.getFromPlayerId());
        assertEquals(2, payload.getToPlayerId());
        assertEquals(200, payload.getAmount());
    }
}
