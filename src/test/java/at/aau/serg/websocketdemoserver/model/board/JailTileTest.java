package at.aau.serg.websocketdemoserver.model.board;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JailTileTest {
    @Test
    void testJailTile() {
        JailTile tile = new JailTile(31);
        assertEquals(31, tile.getIndex());
        assertEquals("Gefängnis", tile.getLabel());
        assertEquals(TileType.PRISON, tile.getType());
    }
}

