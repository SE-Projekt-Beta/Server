package at.aau.serg.websocketdemoserver.model.state;

import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileFactory;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameBoardTest {

    @BeforeEach
    void resetSingleton() {
        GameBoard.setInstance(null);
    }

    @AfterEach
    void cleanup() {
        GameBoard.setInstance(null);
    }

    @Test
    void testSingletonInstanceIsConsistent() {
        GameBoard board1 = GameBoard.get();
        GameBoard board2 = GameBoard.get();

        assertNotNull(board1);
        assertSame(board1, board2);
    }

    @Test
    void testSetInstanceOverridesSingleton() {
        GameBoard dummy = mock(GameBoard.class);
        GameBoard.setInstance(dummy);
        assertSame(dummy, GameBoard.get());
    }

    @Test
    void testGetTilesReturnsCorrectList() {
        GameBoard board = GameBoard.get();
        List<Tile> tiles = board.getTiles();

        assertNotNull(tiles);
        assertFalse(tiles.isEmpty());
        assertTrue(tiles.stream().allMatch(t -> t.getIndex() >= 0));
    }

    @Test
    void testGetTileByValidIndexReturnsTile() {
        GameBoard board = GameBoard.get();
        Tile known = board.getTiles().get(0);
        Tile result = board.getTile(known.getIndex());

        assertNotNull(result);
        assertEquals(known.getIndex(), result.getIndex());
    }

    @Test
    void testGetTileByInvalidIndexThrowsException() {
        GameBoard board = GameBoard.get();
        int invalidIndex = -999;

        assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(invalidIndex));
    }

    @Test
    void testBoardSizeMatchesTileCount() {
        GameBoard board = GameBoard.get();
        assertEquals(board.getTiles().size(), board.size());
    }

}
