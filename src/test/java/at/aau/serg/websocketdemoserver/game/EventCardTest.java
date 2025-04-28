package at.aau.serg.websocketdemoserver.game;

import at.aau.serg.websocketdemoserver.model.cards.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import at.aau.serg.websocketdemoserver.model.board.BankTile;
import at.aau.serg.websocketdemoserver.model.board.RiskTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventCardTest {

    private Player player;

    @BeforeEach
    void setup() {
        player = new Player("Testspieler", new GameBoard());
        player.setCash(1000);
    }

    @Test
    void testBankCardPropertiesAndExecution() {
        BankCard card = new BankCard(1, "Test Bank", "Test Beschreibung");
        assertEquals(1, card.getId());
        assertEquals("Test Bank", card.getTitle());
        assertEquals("Test Beschreibung", card.getDescription());

        // BankCard execute() nur System.out – wir führen aus, aber kein Effekt
        card.execute(player);
        assertEquals(1000, player.getCash()); // unverändert
    }

    @Test
    void testCashBankCardPositiveAmount() {
        CashBankCard card = new CashBankCard(2, "Bankgewinn", "Du erhältst Geld", 200);
        card.execute(player);
        assertEquals(1200, player.getCash());
    }

    @Test
    void testCashBankCardNegativeAmount() {
        CashBankCard card = new CashBankCard(3, "Reparaturkosten", "Du musst zahlen", -150);
        card.execute(player);
        assertEquals(850, player.getCash());
    }

    @Test
    void testRiskCardPropertiesAndExecution() {
        RiskCard card = new RiskCard(4, "Risiko!", "Verliere dein Geld");
        assertEquals(4, card.getId());
        assertEquals("Risiko!", card.getTitle());
        assertEquals("Verliere dein Geld", card.getDescription());

        // RiskCard execute() = nur Ausgabe
        card.execute(player);
        assertEquals(1000, player.getCash()); // unverändert
    }

    @Test
    void testCashRiskCardPositiveAmount() {
        CashRiskCard card = new CashRiskCard(5, "Lottogewinn", "Du hast gewonnen", 500);
        card.execute(player);
        assertEquals(1500, player.getCash());
    }

    @Test
    void testCashRiskCardNegativeAmount() {
        CashRiskCard card = new CashRiskCard(6, "Steuerzahlung", "Du musst zahlen", -100);
        card.execute(player);
        assertEquals(900, player.getCash());
    }

    @Test
    void testEscapeRiskCardExecution() {
        EscapeRiskCard card = new EscapeRiskCard(7, "Freiheitskarte", "Du kannst das Gefängnis verlassen");
        assertFalse(player.hasEscapeCard());

        card.execute(player);
        assertTrue(player.hasEscapeCard());
    }

    @Test
    void testDrawCardFromBankTile() {
        BankTile tile = new BankTile(9);
        ActionCard card = ActionCardFactory.drawCard(tile);
        assertNotNull(card);
    }

    @Test
    void testDrawCardFromRiskTile() {
        RiskTile tile = new RiskTile(23);
        ActionCard card = ActionCardFactory.drawCard(tile);
        assertNotNull(card);
    }

    @Test
    void testDrawCardFromUnknownTile() {
        // Tile, das keine BankTile oder RiskTile ist
        var unknownTile = new at.aau.serg.websocketdemoserver.model.board.SpecialTile(7, "Unbekannt");
        ActionCard card = ActionCardFactory.drawCard(unknownTile);
        assertNull(card);
    }
}