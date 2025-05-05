package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionCardTest {

    // Dummy-Klasse für Test
    static class DummyActionCard extends ActionCard {
        public DummyActionCard(int id, String title, String description) {
            super(id, title, description);
        }

        @Override
        public GameMessage execute(Player player) {
            return GameMessage.error("Test");
        }
    }

    @Test
    void testConstructorAndGetters() {
        ActionCard card = new DummyActionCard(1, "Testkarte", "Du verlierst 50€");

        assertEquals(1, card.getId());
        assertEquals("Testkarte", card.getTitle());
        assertEquals("Du verlierst 50€", card.getDescription());
    }

    @Test
    void testExecuteReturnsExpectedMessage() {
        Player player = mock(Player.class);
        ActionCard card = new DummyActionCard(1, "Dummy", "Beschreibung");

        GameMessage result = card.execute(player);

        assertEquals("Test", result.getPayload());
        assertEquals("ERROR", result.getType().name());
    }
}
