package at.aau.serg.websocketdemoserver.model.state;

import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    @Test
    void testConstructor_createsAllTiles() {
        GameBoard board = new GameBoard();
        List<Tile> tiles = board.getTiles();

        assertNotNull(tiles);
        assertTrue(tiles.size() > 0); // Es sollten Tiles vorhanden sein
    }

    @Test
    void testGetTile_validIndex_returnsTile() {
        GameBoard board = new GameBoard();
        Tile tile = board.getTile(1); // Startfeld laut TileFactory

        assertNotNull(tile);
        assertEquals(1, tile.getIndex());
    }

    @Test
    void testGetTile_invalidIndex_returnsNull() {
        GameBoard board = new GameBoard();
        Tile tile = board.getTile(100); // Index außerhalb des Spielfeldes

        assertNull(tile);
    }

    @Test
    void testGetTiles_returnsSameList() {
        GameBoard board = new GameBoard();
        List<Tile> tiles1 = board.getTiles();
        List<Tile> tiles2 = board.getTiles();

        assertSame(tiles1, tiles2); // sollte dieselbe Referenz sein
    }
}

