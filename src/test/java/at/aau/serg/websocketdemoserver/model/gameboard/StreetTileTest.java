package at.aau.serg.websocketdemoserver.model.gameboard;

import at.aau.serg.websocketdemoserver.model.board.BuildingType;
import at.aau.serg.websocketdemoserver.model.board.StreetLevel;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StreetTileTest {

    private StreetTile tile;

    @BeforeEach
    void setup() {
        tile = new StreetTile(5, "Hauptstraße", 200, 50, StreetLevel.NORMAL, 100);
    }

    @Test
    void testConstructor_initialValues() {
        assertEquals(5, tile.getIndex());
        assertEquals("Hauptstraße", tile.getName());
        assertEquals(200, tile.getPrice());
        assertEquals(50, tile.getBaseRent());
        assertEquals(StreetLevel.NORMAL, tile.getLevel());
        assertEquals(100, tile.getHouseCost());
        assertEquals(200, tile.getHotelCost());
        assertEquals(TileType.STREET, tile.getType());
    }

    @Test
    void testOwnerHandling() {
        Player player = mock(Player.class);
        when(player.getId()).thenReturn(1);
        when(player.getNickname()).thenReturn("Eva");

        tile.setOwner(player);

        assertEquals(player, tile.getOwner());
        assertEquals(1, tile.getOwnerId());
        assertEquals("Eva", tile.getOwnerName());
    }

    @Test
    void testOwnerNullFallbacks() {
        assertEquals(-1, tile.getOwnerId());
        assertEquals("BANK", tile.getOwnerName());
    }

    @Test
    void testBuildHouse_success() {
        assertTrue(tile.buildHouse());
        assertEquals(1, tile.getHouseCount());
        assertEquals(0, tile.getHotelCount());
    }

    @Test
    void testBuildHotel_requires4Houses() {
        // Weniger als 4 Häuser → kein Hotel
        assertFalse(tile.buildHotel());

        tile.buildHouse();
        tile.buildHouse();
        tile.buildHouse();
        tile.buildHouse();

        assertTrue(tile.buildHotel());
        assertEquals(0, tile.getHouseCount());
        assertEquals(1, tile.getHotelCount());
    }

    @Test
    void testPreventTooManyHouses() {
        for (int i = 0; i < 4; i++) {
            assertTrue(tile.buildHouse());
        }
        assertFalse(tile.buildHouse()); // 5. Haus nicht möglich
    }

    @Test
    void testCalculateRent_baseOnly() {
        assertEquals(50, tile.calculateRent());
    }

    @Test
    void testCalculateRent_withHouses() {
        tile.buildHouse(); // 1 house
        assertEquals(100, tile.calculateRent()); // 50 + 50*1*1.0
    }

    @Test
    void testCalculateRent_withHotel() {
        tile.buildHouse();
        tile.buildHouse();
        tile.buildHouse();
        tile.buildHouse();
        tile.buildHotel();
        assertEquals(350, tile.calculateRent()); // 50 + 50*1*6
    }

    @Test
    void testClearBuildings() {
        tile.buildHouse();
        tile.buildHouse();
        tile.clearBuildings();
        assertEquals(0, tile.getHouseCount());
        assertEquals(0, tile.getHotelCount());
    }

    @Test
    void testCalculateRawAndSellValue() {
        // no buildings
        assertEquals(200, tile.calculateRawValue());
        assertEquals(100, tile.calculateSellValue());

        tile.buildHouse(); // +100
        tile.buildHouse(); // +100
        assertEquals(400, tile.calculateRawValue());
        assertEquals(150, tile.calculateSellValue());

        tile.buildHouse();
        tile.buildHouse();
        tile.buildHotel(); // ersetzt Häuser

        assertEquals(400, tile.calculateRawValue());
        assertEquals(150, tile.calculateSellValue());
    }

    @Test
    void testGetBuildingsListIsCopy() {
        tile.buildHouse();
        List<BuildingType> buildings = tile.getBuildings();
        buildings.clear(); // shouldn't affect internal list
        assertEquals(1, tile.getHouseCount());
    }
}
