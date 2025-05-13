package at.aau.serg.websocketdemoserver.model.board;

import java.util.ArrayList;
import java.util.List;

public class TileFactory {

    private TileFactory() {}

    public static List<Tile> createTiles() {
        List<Tile> tiles = new ArrayList<>();

        for (int pos = 0; pos < 40; pos++) {
            Tile generatedTile = StreetTileFactory.createStreetTile(pos);
            if (generatedTile != null) {
                tiles.add(generatedTile);
                continue;
            }

            switch (pos) {
                case 0 -> tiles.add(new SpecialTile(pos, "Start", TileType.START));
                case 2, 22, 37 -> tiles.add(new RiskTile(pos));
                case 8, 27 -> tiles.add(new BankTile(pos));
                case 10 -> tiles.add(new GoToJailTile(pos));
                case 20 -> tiles.add(new SpecialTile(pos, "Sondersteuer", TileType.TAX));
                case 30 -> tiles.add(new JailTile(pos));
                case 32 -> tiles.add(new SpecialTile(pos, "Vermögensabgabe", TileType.TAX));
                default -> {
                    // Unbekanntes Feld oder Freifeld – kein Tile
                }
            }
        }

        return tiles;
    }
}
