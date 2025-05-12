package at.aau.serg.websocketdemoserver.model.board;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GoToJailTileTest {
    @Test
    void testGoToJailTile() {
        GoToJailTile tile = new GoToJailTile(11);
        assertEquals(11, tile.getIndex());
        assertEquals("Polizeikontrolle", tile.getLabel());
        assertEquals(TileType.GOTO_JAIL, tile.getType());
    }
}

