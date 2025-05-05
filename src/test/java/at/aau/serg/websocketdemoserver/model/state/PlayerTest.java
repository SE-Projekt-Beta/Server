package at.aau.serg.websocketdemoserver.model.state;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLostPayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    private Tile startTile;
    private Player player;

    @BeforeEach
    void setUp() {
        startTile = mock(Tile.class);
        when(startTile.getIndex()).thenReturn(0);
        player = new Player(1, "Max", startTile);
    }

    @Test
    void testInitialValues() {
        assertEquals(1, player.getId());
        assertEquals("Max", player.getNickname());
        assertEquals(startTile, player.getCurrentTile());
        assertEquals(1500, player.getCash());
        assertFalse(player.hasEscapeCard());
        assertFalse(player.isSuspended());
        assertEquals(0, player.getSuspensionRounds());
        assertTrue(player.getOwnedStreets().isEmpty());
    }


    @Test
    void testSuspensionLogic() {
        player.suspendForRounds(2);
        assertTrue(player.isSuspended());
        assertEquals(2, player.getSuspensionRounds());

        player.decreaseSuspension();
        assertEquals(1, player.getSuspensionRounds());

        player.resetSuspension();
        assertFalse(player.isSuspended());
    }

    @Test
    void testMoveToTile() {
        GameBoard board = mock(GameBoard.class);
        Tile tile = mock(Tile.class);
        when(board.getTile(3)).thenReturn(tile);

        player.moveToTile(3, board);
        assertEquals(tile, player.getCurrentTile());
    }

    @Test
    void testMoveSteps() {
        Tile tile0 = mock(Tile.class);
        Tile tile1 = mock(Tile.class);
        when(tile0.getIndex()).thenReturn(0);

        GameBoard board = mock(GameBoard.class);
        when(board.getTiles()).thenReturn(List.of(tile0, tile1));
        when(board.getTile(1)).thenReturn(tile1);

        player.setCurrentTile(tile0);
        player.moveSteps(1, board);
        assertEquals(tile1, player.getCurrentTile());
    }

    @Test
    void testPurchaseStreet() {
        StreetTile tile = mock(StreetTile.class);
        when(tile.getIndex()).thenReturn(5);
        when(tile.getOwner()).thenReturn(null);
        when(tile.getPrice()).thenReturn(300);
        when(tile.getLabel()).thenReturn("Straße");

        player.setCurrentTile(tile);
        boolean result = player.purchaseStreet(5);

        assertTrue(result);
        assertEquals(1200, player.getCash());
        assertEquals(List.of(tile), player.getOwnedStreets());
        verify(tile).setOwner(player);
    }

    @Test
    void testPurchaseStreetFails() {
        assertFalse(player.purchaseStreet(1)); // not on StreetTile
    }

    @Test
    void testTransferCashNoBankruptcy() {
        Player receiver = new Player(2, "Eva", startTile);
        GameMessage msg = player.transferCash(receiver, 100);
        assertNull(msg);
        assertEquals(1400, player.getCash());
        assertEquals(1600, receiver.getCash());
    }

    @Test
    void testTransferCashBankruptcy() {
        Player receiver = new Player(2, "Eva", startTile);
        GameMessage msg = player.transferCash(receiver, 1600);
        assertNotNull(msg);
        assertEquals(MessageType.PLAYER_LOST, msg.getType());
        PlayerLostPayload payload = (PlayerLostPayload) msg.getPayload();
        assertEquals(-100, payload.getCash());
        assertEquals(2, receiver.getId());
    }

    @Test
    void testDeductCashNoBankruptcy() {
        GameMessage msg = player.deductCash(500);
        assertNull(msg);
        assertEquals(1000, player.getCash());
    }

    @Test
    void testDeductCashBankruptcy() {
        GameMessage msg = player.deductCash(1600);
        assertNotNull(msg);
        PlayerLostPayload payload = (PlayerLostPayload) msg.getPayload();
        assertEquals(-100, payload.getCash());
        assertEquals(player.getId(), payload.getPlayerId());
    }
}
