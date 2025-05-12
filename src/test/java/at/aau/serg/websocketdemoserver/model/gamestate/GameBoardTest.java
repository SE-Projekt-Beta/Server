package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.model.board.Tile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    @Test
    void testGetTile_validIndex_returnsTile() {
        GameBoard board = new GameBoard();
        Tile tile = board.getTile(1); // Start tile
        assertNotNull(tile);
        assertEquals(1, tile.getIndex());
    }

    @Test
    void testGetTile_invalidIndex_returnsNull() {
        GameBoard board = new GameBoard();
        Tile tile = board.getTile(100); // Out of bounds
        assertNull(tile);
    }

    @Test
    void testGetTiles_containsExpectedSize() {
        GameBoard board = new GameBoard();
        List<Tile> tiles = board.getTiles();
        assertNotNull(tiles);
        assertFalse(tiles.isEmpty());
    }

    @Test
    void testGetTile_consistencyAcrossCalls() {
        GameBoard board = new GameBoard();
        Tile tile1 = board.getTile(5);
        Tile tile2 = board.getTile(5);
        assertSame(tile1, tile2);
    }
}
