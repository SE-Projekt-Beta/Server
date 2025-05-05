package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.dto.EventCardDrawnPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BankCardTest {

    @Test
    void testConstructorAndGetters() {
        BankCard card = new BankCard(2, "Bank zahlt", "Du erhältst 200€");
        assertEquals(2, card.getId());
        assertEquals("Bank zahlt", card.getTitle());
        assertEquals("Du erhältst 200€", card.getDescription());
    }

    @Test
    void testExecute_createsCorrectGameMessage() {
        Player player = mock(Player.class);
        BankCard card = new BankCard(3, "Zufallskarte", "Gehe auf Start");

        GameMessage message = card.execute(player);

        assertEquals(MessageType.EVENT_CARD_DRAWN, message.getType());
        assertTrue(message.getPayload() instanceof EventCardDrawnPayload);

        EventCardDrawnPayload payload = (EventCardDrawnPayload) message.getPayload();
        assertEquals("Zufallskarte", payload.getTitle());
        assertEquals("Gehe auf Start", payload.getDescription());
    }
}
