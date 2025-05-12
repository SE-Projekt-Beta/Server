package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.cards.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventCardServiceTest {

    private BankCardDeck bankCardDeck;
    private RiskCardDeck riskCardDeck;
    private EventCardService service;

    @BeforeEach
    void setUp() {
        bankCardDeck = mock(BankCardDeck.class);
        riskCardDeck = mock(RiskCardDeck.class);
        service = new EventCardService(bankCardDeck, riskCardDeck);
    }

    @Test
    void testDrawBankCard() {
        BankCard mockCard = new BankCard(1, "TestBankCard", "Beschreibung", 100);
        when(bankCardDeck.drawCard()).thenReturn(mockCard);

        BankCard result = service.drawBankCard();

        assertNotNull(result);
        assertEquals("TestBankCard", result.getTitle());
        verify(bankCardDeck).drawCard();
    }

    @Test
    void testDrawRiskCard() {
        RiskCard mockCard = new GoToJailRiskCard(2, "TestRiskCard", "Beschreibung");
        when(riskCardDeck.drawCard()).thenReturn(mockCard);

        RiskCard result = service.drawRiskCard();

        assertNotNull(result);
        assertEquals("TestRiskCard", result.getTitle());
        verify(riskCardDeck).drawCard();
    }
}
