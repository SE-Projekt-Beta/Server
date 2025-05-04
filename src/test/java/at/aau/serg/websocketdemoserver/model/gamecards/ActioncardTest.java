package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionCardTest {

    static class DummyActionCard extends ActionCard {
        public DummyActionCard(int id, String title, String description) {
            super(id, title, description);
        }

        @Override
        public void execute(Player player) {
            // Dummy-Implementation, wird im Test aufrufbar gemacht
        }
    }

    @Test
    void testConstructorAndGetters() {
        DummyActionCard card = new DummyActionCard(1, "TestCard", "Test description");

        assertEquals(1, card.getId());
        assertEquals("TestCard", card.getTitle());
        assertEquals("Test description", card.getDescription());
    }

    @Test
    void testExecuteMethod() {
        DummyActionCard card = new DummyActionCard(2, "ExecuteCard", "Test exec");
        Player player = new Player("TestPlayer", new GameBoard());

        assertDoesNotThrow(() -> card.execute(player));
    }
}

