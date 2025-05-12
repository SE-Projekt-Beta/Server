package at.aau.serg.websocketdemoserver.model.cards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RiskCardDeckTest {

    @Test
    void testSingletonInstance() {
        RiskCardDeck deck1 = RiskCardDeck.get();
        RiskCardDeck deck2 = RiskCardDeck.get();
        assertSame(deck1, deck2);
    }

    @Test
    void testDrawCardReturnsNonNull() {
        RiskCard card = RiskCardDeck.get().drawCard();
        assertNotNull(card);
    }
}
