package at.aau.serg.websocketdemoserver.model.gameboard;


import at.aau.serg.websocketdemoserver.model.board.GoToJailTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoToJailTileTest {

    @Test
    void testConstructorAndType() {
        GoToJailTile tile = new GoToJailTile(30, "Geh ins Gefängnis");

        assertEquals(30, tile.getIndex());
        assertEquals("Geh ins Gefängnis", tile.getLabel());
        assertEquals(TileType.GOTO_JAIL, tile.getType());
    }
}
