package at.aau.serg.websocketdemoserver.gameTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import at.aau.serg.websocketdemoserver.model.tiles.*;

public class TileTest {

    @Test
    void testBankEventTileType() {
        BankEventTile tile = new BankEventTile(6, "Bank Ereignis");
        assertEquals("event_bank", tile.getTileType());
    }

    @Test
    void testRisikoEventTileType() {
        RisikoEventTile tile = new RisikoEventTile(12, "Risiko Ereignis");
        assertEquals("event_risiko", tile.getTileType());
    }

    @Test
    void testGeneralEventTileVariants() {
        GeneralEventTile bank = new GeneralEventTile(6, "Bank Ereignis");
        GeneralEventTile risiko = new GeneralEventTile(2, "Risiko Ereignis");
        GeneralEventTile neutral = new GeneralEventTile(3, "Sonstiges");

        assertEquals("event_bank", bank.getTileType());
        assertEquals("event_risiko", risiko.getTileType());
        assertEquals("event", neutral.getTileType());
    }
}