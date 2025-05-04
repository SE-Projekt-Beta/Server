package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.model.cards.EscapeRiskCard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EscapeRiskCardTest {

    private Player player;

    @BeforeEach
    void setup() {
        GameBoard board = new GameBoard();
        player = new Player("TestPlayer", board);
    }

    @Test
    void testExecute_grantsEscapeCard() {
        EscapeRiskCard card = new EscapeRiskCard(5, "Get Out", "You are free to leave jail");

        assertFalse(player.hasEscapeCard());
        card.execute(player);
        assertTrue(player.hasEscapeCard());

        assertEquals(5, card.getId());
        assertEquals("Get Out", card.getTitle());
        assertEquals("You are free to leave jail", card.getDescription());
    }
}

