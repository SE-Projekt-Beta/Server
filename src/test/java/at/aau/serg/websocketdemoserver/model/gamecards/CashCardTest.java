package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.dto.CashTaskPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.cards.CashBankCard;
import at.aau.serg.websocketdemoserver.model.cards.CashRiskCard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CashCardTest {

    @Test
    void testConstructorValues_BankCard() {
        CashBankCard card = new CashBankCard(3, "Bank", "Beschreibung", 25);
        assertEquals(3, card.getId());
        assertEquals("Bank", card.getTitle());
        assertEquals("Beschreibung", card.getDescription());
    }

    @Test
    void testConstructorValues_RiskCard() {
        CashRiskCard card = new CashRiskCard(4, "Risiko", "Text", -30);
        assertEquals(4, card.getId());
        assertEquals("Risiko", card.getTitle());
        assertEquals("Text", card.getDescription());
    }
}
