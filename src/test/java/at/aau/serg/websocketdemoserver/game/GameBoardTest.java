package at.aau.serg.websocketdemoserver.game;

import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    @Test
    void testGetTileValidIndex() {
        GameBoard board = new GameBoard();
        Tile tile = board.getTile(1);

        assertNotNull(tile);
        assertEquals(1, tile.getIndex());
    }

    @Test
    void testGetTileInvalidIndexReturnsNull() {
        GameBoard board = new GameBoard();
        Tile tile = board.getTile(999);

        assertNull(tile);
    }

    @Test
    void testGetAllTiles() {
        GameBoard board = new GameBoard();
        assertNotNull(board.getTiles());
        assertTrue(board.getTiles().size() > 0);
    }
}