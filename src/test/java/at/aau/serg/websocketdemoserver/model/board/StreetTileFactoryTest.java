package at.aau.serg.websocketdemoserver.model.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreetTileFactoryTest {
    @Test
    void testCreateValidTile() {
        StreetTile tile = StreetTileFactory.createStreetTile(2);
        assertNotNull(tile);
        assertEquals(2, tile.getIndex());
        assertEquals("Amtsplatz", tile.getLabel());
    }

    @Test
    void testCreateInvalidTile() {
        assertNull(StreetTileFactory.createStreetTile(99));
    }
}
