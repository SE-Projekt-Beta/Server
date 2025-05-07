package at.aau.serg.websocketdemoserver.model.board;

import java.util.ArrayList;
import java.util.List;

public class TileFactory {

    private TileFactory() {
        // Prevent instantiation
    }

    public static List<Tile> createTiles() {
        List<Tile> tiles = new ArrayList<>();

        for (int pos = 1; pos <= 40; pos++) {
            Tile generatedTile = StreetTileFactory.createStreetTile(pos);
            if (generatedTile != null) {
                tiles.add(generatedTile);
                continue;
            }

            if (pos == 3 || pos == 23 || pos == 38) tiles.add(new RiskTile(pos));
            if (pos == 9 || pos == 28) tiles.add(new BankTile(pos));

            if (pos == 1) tiles.add(new SpecialTile(pos, "Start"));
            if (pos == 11) tiles.add(new GoToJailTile(pos, "Polizeikontrolle"));
            if (pos == 21) tiles.add(new SpecialTile(pos, "Sondersteuer"));
            if (pos == 31) tiles.add(new JailTile(pos, "Gefängnis"));
            if (pos == 33) tiles.add(new SpecialTile(pos, "Vermögensabgabe"));
        }

        return tiles;
    }
}