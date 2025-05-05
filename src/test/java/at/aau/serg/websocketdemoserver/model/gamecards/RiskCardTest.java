package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.dto.EventCardDrawnPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RiskCardTest {

    @Test
    void testConstructorAndGetters() {
        RiskCard card = new RiskCard(1, "Risiko!", "Ziehe eine Karte.");

        assertEquals(1, card.getId());
        assertEquals("Risiko!", card.getTitle());
        assertEquals("Ziehe eine Karte.", card.getDescription());
    }

    @Test
    void testExecute_returnsCorrectGameMessage() {
        Player player = mock(Player.class);
        RiskCard card = new RiskCard(2, "Testkarte", "Etwas passiert.");

        GameMessage message = card.execute(player);

        assertEquals(MessageType.EVENT_CARD_DRAWN, message.getType());
        assertTrue(message.getPayload() instanceof EventCardDrawnPayload);

        EventCardDrawnPayload payload = (EventCardDrawnPayload) message.getPayload();
        assertEquals("Testkarte", payload.getTitle());
        assertEquals("Etwas passiert.", payload.getDescription());
    }
}
