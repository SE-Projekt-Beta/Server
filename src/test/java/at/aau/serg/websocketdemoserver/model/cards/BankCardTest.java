package at.aau.serg.websocketdemoserver.model.cards;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankCardTest {

    @Test
    void testBankCardFields() {
        BankCard card = new BankCard(1, "Testkarte", "Beschreibung", 100);
        assertEquals(1, card.getId());
        assertEquals("Testkarte", card.getTitle());
        assertEquals("Beschreibung", card.getDescription());
        assertEquals(100, card.getAmount());
    }
}

