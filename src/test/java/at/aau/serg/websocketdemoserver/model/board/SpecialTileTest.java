package at.aau.serg.websocketdemoserver.model.board;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpecialTileTest {
    @Test
    void testSpecialTile() {
        SpecialTile tile = new SpecialTile(21, "Steuer", TileType.TAX);
        assertEquals(21, tile.getIndex());
        assertEquals("Steuer", tile.getLabel());
        assertEquals(TileType.TAX, tile.getType());
    }
}

