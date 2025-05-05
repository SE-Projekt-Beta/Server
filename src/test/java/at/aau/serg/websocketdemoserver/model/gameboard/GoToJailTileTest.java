package at.aau.serg.websocketdemoserver.model.gameboard;

import at.aau.serg.websocketdemoserver.model.board.GoToJailTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoToJailTileTest {

    @Test
    void testConstructor_initializesCorrectly() {
        GoToJailTile tile = new GoToJailTile(15);

        assertEquals(15, tile.getIndex());
        assertEquals("Polizeikontrolle", tile.getLabel());
        assertEquals(TileType.GOTO_JAIL, tile.getType());
    }
}
