package at.aau.serg.websocketdemoserver.model.gameboard;


import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpecialTileTest {

    @Test
    void testConstructorAndGetters() {
        SpecialTile tile = new SpecialTile(4, "Sondersteuer", TileType.TAX);

        assertEquals(4, tile.getIndex());
        assertEquals("Sondersteuer", tile.getLabel());
        assertEquals(TileType.TAX, tile.getType());
    }
}
