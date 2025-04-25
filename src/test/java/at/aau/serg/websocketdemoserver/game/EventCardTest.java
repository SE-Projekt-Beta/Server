package at.aau.serg.websocketdemoserver.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import at.aau.serg.websocketdemoserver.model.tiles.*;

public class EventCardTest {
    @Test
    void testEventCardBankProperties() {
        EventCardBank card = new EventCardBank("Versicherung", -200);
        assertEquals("bank", card.getType());
        assertEquals("Bankkarte", card.getTitle());
        assertEquals("Versicherung", card.getDescription());
        assertEquals(-200, card.getAmount());
    }

    @Test
    void testEventCardRisikoProperties() {
        EventCardRisiko card = new EventCardRisiko("Zurückgehen", -3);
        assertEquals("risiko", card.getType());
        assertEquals("Risikokarte", card.getTitle());
        assertEquals("Zurückgehen", card.getDescription());
        assertEquals(-3, card.getAmount());
    }
}
