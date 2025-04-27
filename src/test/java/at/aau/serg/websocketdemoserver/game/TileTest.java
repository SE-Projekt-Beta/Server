package at.aau.serg.websocketdemoserver.game;

import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void testTileLabelAndIndex() {
        Tile tile = new SpecialTile(5, "Testtile");
        assertEquals(5, tile.getIndex());
        assertEquals("Testtile", tile.getLabel());

        tile.setLabel("Neuer Name");
        assertEquals("Neuer Name", tile.getLabel());
    }

    @Test
    void testStreetTileBasicProperties() {
        StreetTile street = new StreetTile(10, "Teststraße", 200, 20, StreetLevel.NORMAL, 50);

        assertEquals(10, street.getIndex());
        assertEquals("Teststraße", street.getLabel());
        assertEquals(200, street.getPrice());
        assertEquals(20, street.getBaseRent());
        assertEquals(StreetLevel.NORMAL, street.getLevel());
        assertEquals(50, street.getHouseCost());
        assertEquals(100, street.getHotelCost()); // hotelCost = 2 * houseCost
        assertNull(street.getOwner());
        assertTrue(street.getBuildings().isEmpty());
    }

    @Test
    void testStreetTileOwnership() {
        StreetTile street = new StreetTile(2, "Eigentum", 300, 30, StreetLevel.CHEAP, 40);
        Player player = new Player("Alice", new GameBoard());

        street.setOwner(player);
        assertEquals(player, street.getOwner());
    }

    @Test
    void testStreetTileBuildingManagement() {
        StreetTile street = new StreetTile(5, "Bauplatz", 150, 15, StreetLevel.PREMIUM, 60);

        assertTrue(street.addHouse());
        assertEquals(1, street.getHouseCount());
        assertEquals(0, street.getHotelCount());

        street.addHouse();
        street.addHouse();
        street.addHouse();
        assertEquals(4, street.getHouseCount());

        // Jetzt sollte ein Hotel möglich sein
        assertTrue(street.addHotel());
        assertEquals(0, street.getHouseCount());
        assertEquals(1, street.getHotelCount());

        // Kein weiteres House oder Hotel möglich
        assertFalse(street.addHouse());
        assertFalse(street.addHotel());
    }

    @Test
    void testStreetTileRentCalculation() {
        StreetTile cheapStreet = new StreetTile(1, "Billigstraße", 100, 10, StreetLevel.CHEAP, 25);
        StreetTile normalStreet = new StreetTile(2, "Normale Straße", 200, 20, StreetLevel.NORMAL, 50);
        StreetTile premiumStreet = new StreetTile(3, "Teuerstraße", 400, 40, StreetLevel.PREMIUM, 75);

        // Basis-Miete ohne Gebäude
        assertEquals(10, cheapStreet.calculateRent());
        assertEquals(20, normalStreet.calculateRent());
        assertEquals(40, premiumStreet.calculateRent());

        cheapStreet.addHouse();
        normalStreet.addHouse();
        premiumStreet.addHouse();

        // Miete mit 1 Haus (abhängig von Level-Faktor)
        assertEquals(10 + (int)(10 * 0.5), cheapStreet.calculateRent()); // 10 + 5
        assertEquals(20 + (int)(20 * 1.0), normalStreet.calculateRent()); // 20 + 20
        assertEquals(40 + (int)(40 * 1.5), premiumStreet.calculateRent()); // 40 + 60
    }

    @Test
    void testStreetTileValueCalculation() {
        StreetTile street = new StreetTile(7, "Verkaufsstraße", 300, 30, StreetLevel.NORMAL, 50);

        // Ohne Gebäude
        assertEquals(300, street.calculateRawValue());
        assertEquals(150, street.calculateSellValue()); // 50% vom Preis

        // Mit Häusern
        street.addHouse();
        street.addHouse();
        assertEquals(300 + 2 * 50, street.calculateRawValue());
        assertEquals(150 + 2 * (50 * 0.25), street.calculateSellValue());

        // Mit Hotel
        street.addHouse();
        street.addHouse();
        street.addHotel();
        assertEquals(300 + 100, street.calculateRawValue()); // 1 Hotel kostet 100
        assertEquals(150 + 100 * 0.25, street.calculateSellValue());
    }

    @Test
    void testStreetTileBuildingLimits() {
        StreetTile street = new StreetTile(8, "Grenzstraße", 300, 30, StreetLevel.NORMAL, 50);

        // Hotel ohne 4 Häuser -> nicht erlaubt
        street.addHouse();
        assertFalse(street.addHotel());

        street.addHouse();
        street.addHouse();
        street.addHouse();
        assertTrue(street.addHotel()); // Jetzt möglich

        // Nach Hotel keine weiteren Häuser
        assertFalse(street.addHouse());
        assertFalse(street.addHotel());
    }

    @Test
    void testBankTileAndRiskTileActions() {
        BankTile bankTile = new BankTile(9);
        RiskTile riskTile = new RiskTile(12);

        assertNotNull(bankTile.drawBankCard());
        assertNotNull(riskTile.drawRiskCard());
    }

    @Test
    void testSpecialTiles() {
        SpecialTile special = new SpecialTile(15, "Frei Parken");
        JailTile jail = new JailTile(31, "Gefängnis");
        GoToJailTile goToJail = new GoToJailTile(11, "Zurück ins Gefängnis");

        assertEquals(15, special.getIndex());
        assertEquals("Frei Parken", special.getLabel());

        assertEquals(31, jail.getIndex());
        assertEquals("Gefängnis", jail.getLabel());

        assertEquals(11, goToJail.getIndex());
        assertEquals("Zurück ins Gefängnis", goToJail.getLabel());
    }
}