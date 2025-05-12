package at.aau.serg.websocketdemoserver.model.board;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankTileTest {
    @Test
    void testBankTile() {
        BankTile tile = new BankTile(5);
        assertEquals(5, tile.getIndex());
        assertEquals("Bank", tile.getLabel());
        assertEquals(TileType.BANK, tile.getType());
    }
}
