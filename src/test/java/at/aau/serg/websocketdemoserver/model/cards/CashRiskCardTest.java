package at.aau.serg.websocketdemoserver.model.cards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CashRiskCardTest {

    @Test
    void testCashCardFields() {
        CashRiskCard card = new CashRiskCard(3, "Lotto", "Du hast gewonnen!", 150);
        assertEquals(3, card.getId());
        assertEquals("Lotto", card.getTitle());
        assertEquals("Du hast gewonnen!", card.getDescription());
        assertEquals(150, card.getAmount());
        assertEquals(RiskCardEffect.CASH, card.getEffect());
    }
}
