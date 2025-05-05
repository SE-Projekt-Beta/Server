package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import at.aau.serg.websocketdemoserver.model.cards.RiskCardDeck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardDeckTest {

    @Test
    void testBankCardDeckSingletonAndDrawCard() {
        BankCardDeck deck1 = BankCardDeck.get();
        BankCardDeck deck2 = BankCardDeck.get();

        assertSame(deck1, deck2, "BankCardDeck ist kein Singleton");

        BankCard card = deck1.drawCard();
        assertNotNull(card);
        assertInstanceOf(BankCard.class, card);
        assertNotNull(card.getTitle());
        assertNotNull(card.getDescription());
    }

    @Test
    void testRiskCardDeckSingletonAndDrawCard() {
        RiskCardDeck deck1 = RiskCardDeck.get();
        RiskCardDeck deck2 = RiskCardDeck.get();

        assertSame(deck1, deck2, "RiskCardDeck ist kein Singleton");

        RiskCard card = deck1.drawCard();
        assertNotNull(card);
        assertInstanceOf(RiskCard.class, card);
        assertNotNull(card.getTitle());
        assertNotNull(card.getDescription());
    }
}
