package at.aau.serg.websocketdemoserver.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import org.junit.jupiter.api.BeforeEach;

class EventCardServiceTest {

    private EventCardService service;

    @BeforeEach
    void setup() {
        BankCardDeck bankDeck = new BankCardDeck();
        RiskCardDeck riskDeck = new RiskCardDeck();
        service = new EventCardService(bankDeck, riskDeck);
    }

    @Test
    void testDrawBankCardReturnsCard() {
        BankCard card = service.drawBankCard();
        assertNotNull(card);
        assertTrue(card instanceof BankCard);
    }

    @Test
    void testDrawRiskCardReturnsCard() {
        RiskCard card = service.drawRiskCard();
        assertNotNull(card);
        assertTrue(card instanceof RiskCard);
    }
}