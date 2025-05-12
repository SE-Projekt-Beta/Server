package at.aau.serg.websocketdemoserver.model.cards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RiskCardEffectTest {

    @Test
    void testAllEnumValues() {
        assertEquals(RiskCardEffect.CASH, RiskCardEffect.valueOf("CASH"));
        assertEquals(RiskCardEffect.ESCAPE_CARD, RiskCardEffect.valueOf("ESCAPE_CARD"));
        assertEquals(RiskCardEffect.GOTO_JAIL, RiskCardEffect.valueOf("GOTO_JAIL"));
    }

    @Test
    void testEnumCount() {
        assertEquals(3, RiskCardEffect.values().length);
    }
}

