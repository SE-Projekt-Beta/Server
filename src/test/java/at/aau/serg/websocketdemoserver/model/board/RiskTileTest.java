package at.aau.serg.websocketdemoserver.model.board;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RiskTileTest {
    @Test
    void testRiskTile() {
        RiskTile tile = new RiskTile(3);
        assertEquals(3, tile.getIndex());
        assertEquals("Risiko", tile.getLabel());
        assertEquals(TileType.RISK, tile.getType());
    }
}

