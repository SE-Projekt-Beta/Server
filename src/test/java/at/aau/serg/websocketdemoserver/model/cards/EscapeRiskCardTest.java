package at.aau.serg.websocketdemoserver.model.cards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EscapeRiskCardTest {

    @Test
    void testEscapeCard() {
        EscapeRiskCard card = new EscapeRiskCard(2, "Freiheit", "Du bist frei.");
        assertEquals(2, card.getId());
        assertEquals("Freiheit", card.getTitle());
        assertEquals("Du bist frei.", card.getDescription());
        assertEquals(RiskCardEffect.ESCAPE_CARD, card.getEffect());
    }
}
