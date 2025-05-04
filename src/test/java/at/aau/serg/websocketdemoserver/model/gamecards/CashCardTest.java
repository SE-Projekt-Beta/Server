package at.aau.serg.websocketdemoserver.model.gamecards;


import at.aau.serg.websocketdemoserver.model.cards.CashBankCard;
import at.aau.serg.websocketdemoserver.model.cards.CashRiskCard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CashCardTest {

    private Player player;

    @BeforeEach
    void setup() {
        GameBoard board = new GameBoard();
        player = new Player("TestPlayer", board);
    }

    @Test
    void testCashBankCardPositiveAmount() {
        int startCash = player.getCash();
        CashBankCard card = new CashBankCard(1, "Bonus", "You receive a bonus", 200);

        card.execute(player);

        assertEquals(startCash + 200, player.getCash());
        assertEquals(1, card.getId());
        assertEquals("Bonus", card.getTitle());
        assertEquals("You receive a bonus", card.getDescription());
    }

    @Test
    void testCashBankCardNegativeAmount() {
        int startCash = player.getCash();
        CashBankCard card = new CashBankCard(2, "Penalty", "You pay a fine", -150);

        card.execute(player);

        assertEquals(startCash - 150, player.getCash());
        assertEquals(2, card.getId());
        assertEquals("Penalty", card.getTitle());
        assertEquals("You pay a fine", card.getDescription());
    }

    @Test
    void testCashRiskCardPositiveAmount() {
        int startCash = player.getCash();
        CashRiskCard card = new CashRiskCard(3, "Lottery", "You win!", 300);

        card.execute(player);

        assertEquals(startCash + 300, player.getCash());
        assertEquals(3, card.getId());
        assertEquals("Lottery", card.getTitle());
        assertEquals("You win!", card.getDescription());
    }

    @Test
    void testCashRiskCardNegativeAmount() {
        int startCash = player.getCash();
        CashRiskCard card = new CashRiskCard(4, "Loss", "You lose money", -120);

        card.execute(player);

        assertEquals(startCash - 120, player.getCash());
        assertEquals(4, card.getId());
        assertEquals("Loss", card.getTitle());
        assertEquals("You lose money", card.getDescription());
    }
}

