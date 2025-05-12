package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.model.board.BuildingType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private GameBoard board;
    private Player player;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
        player = new Player("Tester", board);
    }

    @Test
    void testInitialValues() {
        assertEquals("Tester", player.getNickname());
        assertTrue(player.isAlive());
        assertEquals(3000, player.getCash());
    }

    @Test
    void testSetAndGetNickname() {
        player.setNickname("Neu");
        assertEquals("Neu", player.getNickname());
    }

    @Test
    void testCashAdjustments() {
        player.addCash(500);
        assertEquals(3500, player.getCash());

        player.deductCash(1000);
        assertEquals(2500, player.getCash());

        assertFalse(player.adjustCash(-2000));
        assertEquals(500, player.getCash());

        assertTrue(player.adjustCash(-600));
        assertFalse(player.isAlive());
        assertEquals(0, player.getCash());
    }

    @Test
    void testEliminate() {
        player.eliminate();
        assertFalse(player.isAlive());
        assertEquals(0, player.getCash());
    }

    @Test
    void testEscapeCardHandling() {
        player.setEscapeCard(true);
        assertTrue(player.hasEscapeCard());
    }

    @Test
    void testSuspensionHandling() {
        assertFalse(player.isSuspended());
        player.suspendForRounds(2);
        assertTrue(player.isSuspended());
        player.decreaseSuspension();
        assertEquals(1, player.getSuspensionRounds());
        player.decreaseSuspension();
        assertFalse(player.isSuspended());
    }

    @Test
    void testStreetPurchaseAndSale() {
        StreetTile street = new StreetTile(42, "Teststraße", 200, 50, null, 100);
        board.getTiles().add(street);

        assertTrue(player.purchaseStreet(42));
        assertTrue(player.getOwnedStreets().contains(street));
        assertEquals(player, street.getOwner());

        assertTrue(player.sellStreet(42));
        assertFalse(player.getOwnedStreets().contains(street));
        assertNull(street.getOwner());
    }

    @Test
    void testInvalidStreetPurchaseAndSale() {
        assertFalse(player.purchaseStreet(999));
        assertFalse(player.sellStreet(999));
    }

    @Test
    void testMovement() {
        Tile tile = board.getTile(1);
        player.moveToTile(1);
        assertEquals(tile, player.getCurrentTile());

        player.moveSteps(3);
        assertNotNull(player.getCurrentTile());
    }

    @Test
    void testSuspendedNoMovement() {
        player.suspendForRounds(1);
        player.moveSteps(4);
        assertNull(player.getCurrentTile());
    }


    @Test
    void testCashTransfer() {
        Player receiver = new Player("Empfänger", board);
        player.transferCash(receiver, 1000);
        assertEquals(2000, player.getCash());
        assertEquals(4000, receiver.getCash());
    }

    @Test
    void testCompareTo() {
        Player p2 = new Player("Zweiter", board);
        assertTrue(player.compareTo(p2) < 0 || player.compareTo(p2) > 0);
    }

    @Test
    void testResetIdCounter() {
        Player.resetIdCounter();
        Player newPlayer = new Player("Neu", board);
        assertEquals(1, newPlayer.getId());
    }
}
