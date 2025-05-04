package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankCardTest {

    private Player player;
    private BankCard card;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer", new GameBoard());
        card = new BankCard(1, "TestCard", "This is a test bank card.");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, card.getId());
        assertEquals("TestCard", card.getTitle());
        assertEquals("This is a test bank card.", card.getDescription());
    }

    @Test
    void testExecuteDoesNotChangePlayerState() {
        int initialCash = player.getCash();
        card.execute(player);
        assertEquals(initialCash, player.getCash());
    }
}

