package at.aau.serg.websocketdemoserver.model.gameboard;

import at.aau.serg.websocketdemoserver.model.board.BankTile;
import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BankTileTest {

    @Test
    void constructor_shouldSetIndexCorrectly() {
        BankTile tile = new BankTile(5);
        assertEquals(5, tile.getIndex());
    }

    @Test
    void drawBankCard_shouldReturnActionCard() {
        BankTile tile = new BankTile(3);
        Optional<ActionCard> card = tile.drawBankCard();
        assertTrue(card.isPresent());
        assertNotNull(card.get().getTitle());
        assertNotNull(card.get().getDescription());
    }
}
