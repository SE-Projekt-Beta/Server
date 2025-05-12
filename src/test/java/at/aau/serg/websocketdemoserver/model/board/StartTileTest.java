package at.aau.serg.websocketdemoserver.model.board;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StartTileTest {
    @Test
    void testStartTile() {
        StartTile tile = new StartTile(1);
        assertEquals(1, tile.getIndex());
        assertEquals("Start", tile.getLabel());
        assertEquals(TileType.START, tile.getType());
    }
}

