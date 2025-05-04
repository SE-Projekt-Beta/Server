package at.aau.serg.websocketdemoserver.model.gameboard;

import at.aau.serg.websocketdemoserver.model.board.JailTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JailTileTest {

    @Test
    void testConstructorAndType() {
        JailTile tile = new JailTile(10, "Gefängnis");

        assertEquals(10, tile.getIndex());
        assertEquals("Gefängnis", tile.getLabel());
        assertEquals(TileType.PRISON, tile.getType());
    }
}
