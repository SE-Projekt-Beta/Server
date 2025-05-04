package at.aau.serg.websocketdemoserver.model.gameboard;


import at.aau.serg.websocketdemoserver.model.board.StreetLevel;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreetTileTest {

    private StreetTile tile;

    @BeforeEach
    void setUp() {
        tile = new StreetTile(5, "Hauptstraße", 500, 50, StreetLevel.NORMAL, 100);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(5, tile.getIndex());
        assertEquals("Hauptstraße", tile.getLabel());
        assertEquals(500, tile.getPrice());
        assertEquals(50, tile.getBaseRent());
        assertEquals(StreetLevel.NORMAL, tile.getLevel());
        assertEquals(100, tile.getHouseCost());
        assertEquals(200, tile.getHotelCost()); // hotelCost = houseCost * 2
        assertEquals(TileType.STREET, tile.getType());
        assertTrue(tile.getBuildings().isEmpty());
    }

    @Test
    void testSetAndGetOwner() {
        Player player = new Player("Max", new GameBoard());
        tile.setOwner(player);
        assertEquals(player, tile.getOwner());
    }

    @Test
    void testCalculateRent_NoBuildings() {
        assertEquals(50, tile.calculateRent());
    }

    @Test
    void testCalculateRent_WithHouses() {
        tile.addHouse();
        tile.addHouse();
        assertEquals(50 + 50 * 2 * 1.0, tile.calculateRent());
    }

    @Test
    void testCalculateRent_WithHotel() {
        tile.addHouse();
        tile.addHouse();
        tile.addHouse();
        tile.addHouse();
        tile.addHotel(); // replaces 4 houses
        assertEquals(50 + 50 * 6 * 1.0, tile.calculateRent());
    }

    @Test
    void testAddHouseLimits() {
        assertTrue(tile.addHouse());
        assertTrue(tile.addHouse());
        assertTrue(tile.addHouse());
        assertTrue(tile.addHouse());
        assertFalse(tile.addHouse()); // max 4
    }

    @Test
    void testAddHotelConditions() {
        // Not enough houses
        tile.clearBuildings();
        tile.addHouse();
        assertFalse(tile.addHotel());

        // Add 4 houses and then hotel
        tile.clearBuildings();
        tile.addHouse();
        tile.addHouse();
        tile.addHouse();
        tile.addHouse();
        assertTrue(tile.addHotel());
        assertEquals(1, tile.getHotelCount());
        assertEquals(0, tile.getHouseCount());
    }

    @Test
    void testClearBuildings() {
        tile.addHouse();
        tile.addHouse();
        tile.clearBuildings();
        assertEquals(0, tile.getBuildings().size());
    }

    @Test
    void testCalculateRawValueAndSellValue() {
        tile.addHouse();
        tile.addHouse();
        tile.addHotel();

        int raw = tile.calculateRawValue(); // 500 + 0*house + 1*hotel
        int expectedRaw = 500 + 200;
        assertEquals(expectedRaw, raw);

        int expectedSell = (int)(500 * 0.5 + 200 * 0.25);
        assertEquals(expectedSell, tile.calculateSellValue());
    }
}
