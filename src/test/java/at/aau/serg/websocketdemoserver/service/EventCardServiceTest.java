package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.cards.CashRiskCard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventCardServiceTest {
    @Test
    void testDrawBankCard() {
        EventCardService service = new EventCardService();
        CashRiskCard card = service.drawCard("bank");
        assertEquals("bank", card.getType());
    }

    @Test
    void testDrawRisikoCard() {
        EventCardService service = new EventCardService();
        CashRiskCard card = service.drawCard("risiko");
        assertEquals("risiko", card.getType());
    }

    @Test
    void testDrawInvalidCardType() {
        EventCardService service = new EventCardService();
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.drawCard("foo"));
        assertTrue(e.getMessage().contains("Unbekannter Event-Typ"));
    }
}
