package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.model.cards.EscapeRiskCard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EscapeRiskCardTest {

    @Test
    void testConstructorAndGetters() {
        EscapeRiskCard card = new EscapeRiskCard(1, "Freiheitskarte", "Du darfst aus dem Gefängnis frei.");

        assertEquals(1, card.getId());
        assertEquals("Freiheitskarte", card.getTitle());
        assertEquals("Du darfst aus dem Gefängnis frei.", card.getDescription());
    }

    @Test
    void testExecute_setsEscapeCardAndReturnsMessage() {
        Player player = mock(Player.class);
        when(player.getId()).thenReturn(42);
        when(player.getNickname()).thenReturn("Max");

        EscapeRiskCard card = new EscapeRiskCard(2, "Frei", "Verlasse das Gefängnis");

        GameMessage result = card.execute(player);

        verify(player).setEscapeCard(true);

        assertEquals(MessageType.PLAYER_OUT_OF_JAIL_CARD, result.getType());
        assertTrue(result.getPayload() instanceof PlayerOutOfJailCardPayload);

        PlayerOutOfJailCardPayload payload = (PlayerOutOfJailCardPayload) result.getPayload();
        assertEquals(42, payload.getPlayerId());
        assertEquals("Max", payload.getMessage());
    }
}
