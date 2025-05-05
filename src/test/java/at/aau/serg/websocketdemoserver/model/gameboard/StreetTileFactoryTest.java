package at.aau.serg.websocketdemoserver.model.gameboard;


import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.StreetTileFactory;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreetTileFactoryTest {

    @Test
    void testKnownPositions() {
        StreetTile tile = StreetTileFactory.createStreetTile(2);
        assertNotNull(tile);
        assertEquals("Amtsplatz", tile.getLabel());
        assertEquals(220, tile.getPrice());
        assertEquals(TileType.STREET, tile.getType());
    }

    @Test
    void testAllValidPositionsReturnTiles() {
        int[] validPositions = {
                2, 5, 6, 7, 10, 12, 15, 16, 17, 19,
                20, 22, 25, 26, 27, 29, 30, 32, 35, 36, 37, 39, 40
        };

        for (int pos : validPositions) {
            StreetTile tile = StreetTileFactory.createStreetTile(pos);
            assertNotNull(tile, "Expected non-null for position: " + pos);
            assertEquals(pos, tile.getIndex());
        }
    }

    @Test
    void testInvalidPositionReturnsNull() {
        assertNull(StreetTileFactory.createStreetTile(1));
        assertNull(StreetTileFactory.createStreetTile(100));
        assertNull(StreetTileFactory.createStreetTile(-1));
    }

    @Test
    void testPrivateConstructorCoverage() throws Exception {
        var constructor = StreetTileFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance(); // for coverage
    }
}
