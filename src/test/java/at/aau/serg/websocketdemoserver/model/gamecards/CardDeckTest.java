package at.aau.serg.websocketdemoserver.model.gamecards;


import at.aau.serg.websocketdemoserver.model.cards.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardDecksTest {

    @RepeatedTest(10)
    void testDrawRandomRiskCard_returnsValidCard() {
        RiskCardDeck deck = new RiskCardDeck();
        RiskCard card = deck.drawRandomRiskCard();

        assertNotNull(card);
        assertNotNull(card.getTitle());
        assertNotNull(card.getDescription());

        Player player = new Player("RiskTester", new GameBoard());
        card.execute(player);
    }

    @RepeatedTest(10)
    void testDrawRandomBankCard_returnsValidCard() {
        BankCardDeck deck = new BankCardDeck();
        BankCard card = deck.drawRandomBankCard();

        assertNotNull(card);
        assertNotNull(card.getTitle());
        assertNotNull(card.getDescription());

        Player player = new Player("BankTester", new GameBoard());
        card.execute(player);
    }

    @Test
    void testCashBankCardModifiesPlayerCash() {
        Player player = new Player("CashBank", new GameBoard());
        int original = player.getCash();

        CashBankCard card = new CashBankCard(99, "TestCard", "Add 500", 500);
        card.execute(player);
        assertEquals(original + 500, player.getCash());

        card = new CashBankCard(100, "LoseMoney", "Minus 200", -200);
        card.execute(player);
        assertEquals(original + 300, player.getCash()); // 500 - 200
    }

    @Test
    void testCashRiskCardModifiesPlayerCash() {
        Player player = new Player("CashRisk", new GameBoard());
        int original = player.getCash();

        CashRiskCard card = new CashRiskCard(77, "Lottery", "Win 300", 300);
        card.execute(player);
        assertEquals(original + 300, player.getCash());
    }

    @Test
    void testEscapeRiskCardGrantsEscape() {
        Player player = new Player("Escape", new GameBoard());
        assertFalse(player.hasEscapeCard());

        EscapeRiskCard card = new EscapeRiskCard(55, "Get Out", "Escape jail");
        card.execute(player);

        assertTrue(player.hasEscapeCard());
    }
}

