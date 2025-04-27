package at.aau.serg.websocketdemoserver.game;

import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventCardTest {
    @Test
    void testEventCardBankProperties() {
        BankCard card = new BankCard("Versicherung", -200);
        assertEquals("bank", card.getType());
        assertEquals("Bankkarte", card.getTitle());
        assertEquals("Versicherung", card.getDescription());
        assertEquals(-200, card.getAmount());
    }

    @Test
    void testEventCardRisikoProperties() {
        RiskCard card = new RiskCard("Zurückgehen", -3);
        assertEquals("risiko", card.getType());
        assertEquals("Risikokarte", card.getTitle());
        assertEquals("Zurückgehen", card.getDescription());
        assertEquals(-3, card.getAmount());
    }
}
