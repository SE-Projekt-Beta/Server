package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.cards.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventCardServiceTest {

    @Test
    void testGetInstance() {
        EventCardService service1 = EventCardService.get();
        EventCardService service2 = EventCardService.get();
        assertNotNull(service1);
        assertSame(service1, service2, "Singleton-Instanz muss gleich sein");
    }

    @Test
    void testDrawBankCard() {
        BankCard expectedCard = mock(BankCard.class);
        BankCardDeck mockDeck = mock(BankCardDeck.class);
        when(mockDeck.drawCard()).thenReturn(expectedCard);

        try (MockedStatic<BankCardDeck> staticMock = mockStatic(BankCardDeck.class)) {
            staticMock.when(BankCardDeck::get).thenReturn(mockDeck);

            BankCard actual = EventCardService.get().drawBankCard();
            assertEquals(expectedCard, actual);
        }
    }

    @Test
    void testDrawRiskCard() {
        RiskCard expectedCard = mock(RiskCard.class);
        RiskCardDeck mockDeck = mock(RiskCardDeck.class);
        when(mockDeck.drawCard()).thenReturn(expectedCard);

        try (MockedStatic<RiskCardDeck> staticMock = mockStatic(RiskCardDeck.class)) {
            staticMock.when(RiskCardDeck::get).thenReturn(mockDeck);

            RiskCard actual = EventCardService.get().drawRiskCard();
            assertEquals(expectedCard, actual);
        }
    }
}
