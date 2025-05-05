package at.aau.serg.websocketdemoserver.model.board;

public class StreetTileFactory {

    private StreetTileFactory() {
        // Prevent instantiation
    }

    public static StreetTile createStreetTile(int position) {
        return StreetData.STREETS.stream()
                .filter(def -> def.index() == position)
                .findFirst()
                .map(def -> new StreetTile(
                        def.index(),
                        def.name(),
                        def.price(),
                        def.baseRent(),
                        def.level(),
                        def.houseCost(),
                        def.hotelCost()
                ))
                .orElse(null);
    }
}
