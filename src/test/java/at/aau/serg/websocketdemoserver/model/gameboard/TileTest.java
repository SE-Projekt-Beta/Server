package at.aau.serg.websocketdemoserver.model.gameboard;


import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    // Test-Stub für die abstrakte Klasse Tile
    private static class TestTile extends Tile {
        public TestTile(int index) {
            super(index);
        }

        @Override
        public TileType getType() {
            return TileType.BANK;
        }
    }

    @Test
    void testTileGettersAndSetters() {
        Tile tile = new TestTile(7);
        tile.setLabel("Testplatz");

        assertEquals(7, tile.getIndex());
        assertEquals("Testplatz", tile.getLabel());
        assertEquals(TileType.BANK, tile.getType());
    }
}
