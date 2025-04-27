package at.aau.serg.websocketdemoserver.game;

import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    @Test
    void testBoardHas40Tiles() {
        GameBoard board = new GameBoard();
        for (int i = 0; i < 40; i++) {
            Tile t = board.getTileAt(i);
            assertNotNull(t);
            assertEquals(i, t.getPosition());
        }
    }

    @Test
    void testBoardWrapsCorrectly() {
        GameBoard board = new GameBoard();
        Tile t = board.getTileAt(80); // 80 % 40 = 0
        assertEquals(0, t.getPosition());
    }
}

