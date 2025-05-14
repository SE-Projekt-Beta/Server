package at.aau.serg.websocketdemoserver.model.cards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankCardDeckTest {

    @Test
    void testSingletonInstance() {
        BankCardDeck deck1 = BankCardDeck.get();
        BankCardDeck deck2 = BankCardDeck.get();
        assertSame(deck1, deck2);
    }

    @Test
    void testDrawCardReturnsNonNull() {
        BankCard card = BankCardDeck.get().drawCard();
        assertNotNull(card);
    }
}
