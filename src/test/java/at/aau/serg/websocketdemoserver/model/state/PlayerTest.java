package at.aau.serg.websocketdemoserver.model.state;

import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
        Player.resetIdCounter();
        player = new Player("Max", board);
    }

    @Test
    void testBasicAttributes() {
        assertEquals("Max", player.getNickname());
        assertEquals(3000, player.getCash());
        assertFalse(player.hasEscapeCard());
        assertFalse(player.isSuspended());
        assertFalse(player.hasCheated());
        assertEquals(-1, player.getPosition());
    }

    @Test
    void testCashAndEscapeCard() {
        player.setCash(1000);
        assertEquals(1000, player.getCash());

        player.setEscapeCard(true);
        assertTrue(player.hasEscapeCard());
    }

    @Test
    void testSuspension() {
        player.suspendForRounds(2);
        assertTrue(player.isSuspended());
        assertEquals(2, player.getSuspensionRounds());

        player.decreaseSuspension();
        assertEquals(1, player.getSuspensionRounds());

        player.resetSuspension();
        assertFalse(player.isSuspended());
    }

    @Test
    void testCheatFlag() {
        player.setCheatFlag(true);
        assertTrue(player.hasCheated());
    }


    @Test
    void testPurchaseAndSellStreet() {
        boolean purchased = player.purchaseStreet(5);
        assertTrue(purchased);
        assertEquals(1, player.getOwnedStreets().size());

        int cashAfterPurchase = player.getCash();

        boolean sold = player.sellStreet(5);
        assertTrue(sold);
        assertEquals(0, player.getOwnedStreets().size());
        assertTrue(player.getCash() > cashAfterPurchase);
    }

    @Test
    void testInvalidPurchaseAndSell() {
        assertFalse(player.purchaseStreet(100)); // invalid position
        assertFalse(player.sellStreet(100));     // invalid position
    }

    @Test
    void testTransferCash() {
        Player receiver = new Player("Lisa", board);
        int senderCashBefore = player.getCash();
        int receiverCashBefore = receiver.getCash();

        player.transferCash(receiver, 500);

        assertEquals(senderCashBefore - 500, player.getCash());
        assertEquals(receiverCashBefore + 500, receiver.getCash());
    }

    @Test
    void testSetNicknameAndPosition() {
        player.setNickname("John");
        assertEquals("John", player.getNickname());

        player.setPosition(10);
        assertEquals(10, player.getCurrentTile().getIndex());
    }

    @Test
    void testCompareTo() {
        Player p2 = new Player("Zoe", board);
        assertTrue(player.compareTo(p2) < 0);
    }
}
