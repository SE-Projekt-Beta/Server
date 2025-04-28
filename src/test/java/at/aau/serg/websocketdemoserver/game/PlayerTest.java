package at.aau.serg.websocketdemoserver.game;

import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.BuildingType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayerTest {

    private GameBoard board;
    private Player player;

    @BeforeEach
    void setup() {
        board = mock(GameBoard.class);
        player = new Player("Alice", board);
    }

    @Test
    void testConstructorAndDefaults() {
        assertEquals("Alice", player.getNickname());
        assertEquals(3000, player.getCash());
        assertEquals(0, player.getSuspensionRounds());
        assertFalse(player.hasEscapeCard());
        assertFalse(player.hasCheated());
        assertTrue(player.getOwnedStreets().isEmpty());
        assertNull(player.getCurrentTile());
    }

    @Test
    void testSettersAndGetters() {
        player.setNickname("Bob");
        assertEquals("Bob", player.getNickname());

        Tile tile = mock(Tile.class);
        player.setCurrentTile(tile);
        assertEquals(tile, player.getCurrentTile());

        player.setCash(5000);
        assertEquals(5000, player.getCash());

        player.setEscapeCard(true);
        assertTrue(player.hasEscapeCard());

        player.setCheatFlag(true);
        assertTrue(player.hasCheated());
    }

    @Test
    void testSuspensionManagement() {
        assertFalse(player.isSuspended());

        player.suspendForRounds(2);
        assertTrue(player.isSuspended());
        assertEquals(2, player.getSuspensionRounds());

        player.decreaseSuspension();
        assertEquals(1, player.getSuspensionRounds());

        player.resetSuspension();
        assertEquals(0, player.getSuspensionRounds());
        assertFalse(player.isSuspended());
    }

    @Test
    void testPurchaseStreetSuccess() {
        StreetTile street = mock(StreetTile.class);
        when(street.getOwner()).thenReturn(null);
        when(street.getPrice()).thenReturn(500);
        when(board.getTile(3)).thenReturn(street);

        boolean result = player.purchaseStreet(3);

        assertTrue(result);
        assertEquals(2500, player.getCash());
        assertEquals(1, player.getOwnedStreets().size());
        verify(street).setOwner(player);
    }

    @Test
    void testPurchaseStreetFailsIfNotStreet() {
        Tile tile = mock(Tile.class);
        when(board.getTile(2)).thenReturn(tile);

        boolean result = player.purchaseStreet(2);

        assertFalse(result);
        assertEquals(3000, player.getCash());
        assertTrue(player.getOwnedStreets().isEmpty());
    }

    @Test
    void testPurchaseStreetFailsIfOwned() {
        StreetTile street = mock(StreetTile.class);
        when(street.getOwner()).thenReturn(mock(Player.class));
        when(board.getTile(5)).thenReturn(street);

        boolean result = player.purchaseStreet(5);

        assertFalse(result);
    }

    @Test
    void testPurchaseStreetFailsIfTooExpensive() {
        StreetTile street = mock(StreetTile.class);
        when(street.getOwner()).thenReturn(null);
        when(street.getPrice()).thenReturn(4000);
        when(board.getTile(8)).thenReturn(street);

        boolean result = player.purchaseStreet(8);

        assertFalse(result);
    }

    @Test
    void testSellStreetSuccess() {
        StreetTile street = mock(StreetTile.class);
        when(street.getOwner()).thenReturn(player);
        when(street.calculateSellValue()).thenReturn(300);
        when(board.getTile(10)).thenReturn(street);

        player.getOwnedStreets().add(street);

        boolean result = player.sellStreet(10);

        assertTrue(result);
        assertEquals(3300, player.getCash());
        assertTrue(player.getOwnedStreets().isEmpty());
        verify(street).clearBuildings();
        verify(street).setOwner(null);
    }

    @Test
    void testSellStreetFailsIfNotStreet() {
        Tile tile = mock(Tile.class);
        when(board.getTile(11)).thenReturn(tile);

        boolean result = player.sellStreet(11);
    }
}