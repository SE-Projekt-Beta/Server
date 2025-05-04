package at.aau.serg.websocketdemoserver.model.gamecards;


import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskCardTest {

    private RiskCard card;
    private Player player;

    @BeforeEach
    void setUp() {
        card = new RiskCard(2, "Test Risk", "You lose a turn.");
        player = new Player("Risky", new GameBoard());
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(2, card.getId());
        assertEquals("Test Risk", card.getTitle());
        assertEquals("You lose a turn.", card.getDescription());
    }

    @Test
    void testExecutePrintsAction() {
        // just make sure it doesn't throw and uses player info
        assertDoesNotThrow(() -> card.execute(player));
    }
}

