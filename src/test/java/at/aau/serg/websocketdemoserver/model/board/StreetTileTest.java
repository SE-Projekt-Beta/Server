package at.aau.serg.websocketdemoserver.model.board;

import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StreetTileTest {

    private StreetTile tile;
    private Player player;
    private GameBoard dummyBoard;

    @BeforeEach
    void setUp() {
        dummyBoard = mock(GameBoard.class);
        player = new Player("Tester", dummyBoard);
        player.setCash(1000);
        tile = new StreetTile(1, "Teststraße", 100, 50, StreetLevel.NORMAL, 50, 100);
        tile.setOwner(player);
    }

    @Test
    void testCalculateRent_noBuildings() {
        assertEquals(50, tile.calculateRent());
    }

    @Test
    void testCalculateRent_withHouses() {
        tile.buildHouse(player);
        tile.buildHouse(player);
        assertEquals(150, tile.calculateRent()); // 50 + 50*2*1.0
    }

    @Test
    void testCalculateRent_withHotel() {
        for (int i = 0; i < 4; i++) tile.buildHouse(player);
        tile.buildHotel(player);
        assertEquals(350, tile.calculateRent()); // 50 + 50*6*1.0
    }

    @Test
    void testBuildHouse_success() {
        assertTrue(tile.buildHouse(player));
        assertEquals(1, tile.getHouseCount());
        assertEquals(950, player.getCash());
    }


    @Test
    void testBuildHouse_invalidConditions() {
        Player other = new Player("Other", dummyBoard);
        assertFalse(tile.buildHouse(other));
        for (int i = 0; i < 4; i++) tile.buildHouse(player);
        assertFalse(tile.buildHouse(player));
    }

    @Test
    void testBuildHotel_invalidConditions() {
        Player other = new Player("Other", dummyBoard);
        assertFalse(tile.buildHotel(other));
        tile.buildHouse(player); // nur 1 Haus
        assertFalse(tile.buildHotel(player));
    }

    @Test
    void testClearBuildings() {
        tile.buildHouse(player);
        tile.clearBuildings();
        assertEquals(0, tile.getHouseCount());
    }

    @Test
    void testOwnerMethods() {
        assertEquals(player, tile.getOwner());
        assertTrue(tile.isOwner(player));
        assertEquals(player.getId(), tile.getOwnerId());
        assertEquals("Tester", tile.getOwnerName());

        tile.setOwner(null);
        assertEquals(-1, tile.getOwnerId());
        assertEquals("BANK", tile.getOwnerName());
    }

    @Test
    void testMetaInformation() {
        assertEquals(100, tile.getPrice());
        assertEquals(50, tile.getBaseRent());
        assertEquals(StreetLevel.NORMAL, tile.getLevel());
        assertEquals(50, tile.getHouseCost());
        assertEquals(100, tile.getHotelCost());
        assertEquals("Teststraße", tile.getName());
        assertEquals(TileType.STREET, tile.getType());
    }

    @Test
    void testGetBuildingsListImmutable() {
        tile.buildHouse(player);
        var buildings = tile.getBuildings();
        assertEquals(1, buildings.size());
        buildings.clear();
        assertEquals(1, tile.getBuildings().size()); // original bleibt
    }
}
