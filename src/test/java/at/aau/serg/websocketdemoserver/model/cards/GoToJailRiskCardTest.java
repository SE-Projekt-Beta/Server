package at.aau.serg.websocketdemoserver.model.cards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GoToJailRiskCardTest {

    @Test
    void testGoToJailCard() {
        GoToJailRiskCard card = new GoToJailRiskCard(1, "Ins Gefängnis", "Ab ins Gefängnis!");
        assertEquals(1, card.getId());
        assertEquals("Ins Gefängnis", card.getTitle());
        assertEquals("Ab ins Gefängnis!", card.getDescription());
        assertEquals(RiskCardEffect.GOTO_JAIL, card.getEffect());
    }
}
