package at.aau.serg.websocketdemoserver.model.gameboard;


import at.aau.serg.websocketdemoserver.model.board.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TileFactoryTest {

    @Test
    void testCreateTiles_containsExpectedTiles() {
        List<Tile> tiles = TileFactory.createTiles();
        assertNotNull(tiles);
        assertTrue(tiles.size() > 0);

        // Sicherstellen, dass einige erwartete Felder vorhanden sind
        assertTrue(tiles.stream().anyMatch(t -> t instanceof StreetTile));
        assertTrue(tiles.stream().anyMatch(t -> t instanceof RiskTile && t.getIndex() == 3));
        assertTrue(tiles.stream().anyMatch(t -> t instanceof BankTile && t.getIndex() == 9));
        assertTrue(tiles.stream().anyMatch(t -> t instanceof SpecialTile && t.getLabel().equals("Start")));
        assertTrue(tiles.stream().anyMatch(t -> t instanceof GoToJailTile && t.getIndex() == 11));
        assertTrue(tiles.stream().anyMatch(t -> t instanceof JailTile && t.getIndex() == 31));
    }

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<TileFactory> constructor = TileFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        TileFactory instance = constructor.newInstance();
        assertNotNull(instance); // Damit der Konstruktor abgedeckt ist
    }
}
