package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.cards.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventCardServiceTest {

    private BankCardDeck mockBankDeck;
    private RiskCardDeck mockRiskDeck;
    private EventCardService service;

    @BeforeEach
    void setUp() {
        mockBankDeck = mock(BankCardDeck.class);
        mockRiskDeck = mock(RiskCardDeck.class);
        service = new EventCardService(mockBankDeck, mockRiskDeck);
    }

    @Test
    void testDrawBankCard() {
        BankCard expected = new BankCard(1, "Test", "Test Bank Card");
        when(mockBankDeck.drawRandomBankCard()).thenReturn(expected);

        BankCard result = service.drawBankCard();

        assertNotNull(result);
        assertEquals(expected, result);
        verify(mockBankDeck).drawRandomBankCard();
    }

    @Test
    void testDrawRiskCard() {
        RiskCard expected = new RiskCard(2, "Risk", "Test Risk Card");
        when(mockRiskDeck.drawRandomRiskCard()).thenReturn(expected);

        RiskCard result = service.drawRiskCard();

        assertNotNull(result);
        assertEquals(expected, result);
        verify(mockRiskDeck).drawRandomRiskCard();
    }
}
