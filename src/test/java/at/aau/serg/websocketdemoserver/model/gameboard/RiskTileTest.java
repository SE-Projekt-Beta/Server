package at.aau.serg.websocketdemoserver.model.gameboard;


import at.aau.serg.websocketdemoserver.model.board.RiskTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RiskTileTest {

    @Test
    void testConstructorAndGetType() {
        RiskTile tile = new RiskTile(8);

        assertEquals(8, tile.getIndex());
        assertEquals(TileType.RISK, tile.getType());
    }

    @Test
    void testDrawRiskCard() {
        RiskTile tile = new RiskTile(5);
        Optional<ActionCard> card = tile.drawRiskCard();

        assertTrue(card.isPresent());
        assertNotNull(card.get().getTitle());
    }
}
