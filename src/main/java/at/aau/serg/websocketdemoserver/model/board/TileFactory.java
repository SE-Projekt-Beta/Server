package at.aau.serg.websocketdemoserver.model.board;

import java.util.ArrayList;
import java.util.List;

public class TileFactory {

    private TileFactory() {}

    public static List<Tile> createTiles() {
        List<Tile> tiles = new ArrayList<>();

        for (int pos = 1; pos <= 40; pos++) {
            Tile generatedTile = StreetTileFactory.createStreetTile(pos);
            if (generatedTile != null) {
                tiles.add(generatedTile);
                continue;
            }

            switch (pos) {
                case 1 -> tiles.add(new SpecialTile(pos, "Start", TileType.START));
                case 3, 23, 38 -> tiles.add(new RiskTile(pos));
                case 5, 9, 28 -> tiles.add(new BankTile(pos));
                case 11 -> tiles.add(new GoToJailTile(pos));
                case 21 -> tiles.add(new SpecialTile(pos, "Sondersteuer", TileType.TAX));
                case 31 -> tiles.add(new JailTile(pos));
                case 33 -> tiles.add(new SpecialTile(pos, "Vermögensabgabe", TileType.TAX));
                default -> {
                    // Unbekanntes Feld oder Freifeld – kein Tile
                }
            }
        }

        return tiles;
    }
}