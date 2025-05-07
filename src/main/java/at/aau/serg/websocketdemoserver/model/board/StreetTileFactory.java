package at.aau.serg.websocketdemoserver.model.board;


public class StreetTileFactory {

    private StreetTileFactory() {
        // Prevent instantiation
    }

    public static StreetTile createStreetTile(int position) {
        switch (position) {
            case 2 -> new StreetTile(2, "Amtsplatz", 220, 80, StreetLevel.CHEAP, 160);
            case 5 -> new StreetTile(5, "Murplatz", 300, 120, StreetLevel.NORMAL, 200);
            case 6 -> new StreetTile(6, "Annenstraße", 250, 96, StreetLevel.NORMAL, 150);
            case 7 -> new StreetTile(7, "Joaneumring", 220, 80, StreetLevel.NORMAL, 130);
            case 10 -> new StreetTile(10, "Joseph-Haydn-Gasse", 100, 24, StreetLevel.CHEAP, 50);
            case 12 -> new StreetTile(12, "Schlossgrund", 220, 80, StreetLevel.CHEAP, 160);
            case 15 -> new StreetTile(15, "Kärntner Straße", 380, 200, StreetLevel.PREMIUM, 220);
            case 16 -> new StreetTile(16, "Mariahilfer Straße", 350, 160, StreetLevel.PREMIUM, 220);
            case 17 -> new StreetTile(17, "Kobenzlstraße", 250, 96, StreetLevel.NORMAL, 150);
            case 19 -> new StreetTile(19, "Landstraße", 300, 120, StreetLevel.NORMAL, 200);
            case 20 -> new StreetTile(20, "Stifterstraße", 180, 56, StreetLevel.PREMIUM, 100);
            case 22 -> new StreetTile(22, "Museumstraße", 220, 80, StreetLevel.CHEAP, 160);
            case 25 -> new StreetTile(25, "Mirabellplatz", 250, 96, StreetLevel.NORMAL, 150);
            case 26 -> new StreetTile(26, "Westbahnstraße", 240, 88, StreetLevel.NORMAL, 140);
            case 27 -> new StreetTile(27, "Universitätsplatz", 250, 96, StreetLevel.NORMAL, 150);
            case 29 -> new StreetTile(29, "Burggasse", 140, 40, StreetLevel.NORMAL, 100);
            case 30 -> new StreetTile(30, "Villacherstraße", 200, 64, StreetLevel.NORMAL, 110);
            case 32 -> new StreetTile(32, "Alter Platz", 210, 72, StreetLevel.NORMAL, 120);
            case 35 -> new StreetTile(35, "Maria-Theresien-Straße", 300, 120, StreetLevel.NORMAL, 200);
            case 36 -> new StreetTile(36, "Andreas-Hofer-Straße", 250, 96, StreetLevel.NORMAL, 150);
            case 37 -> new StreetTile(37, "Boznerplatz", 300, 120, StreetLevel.NORMAL, 200);
            case 39 -> new StreetTile(39, "Arlbergstraße", 120, 32, StreetLevel.NORMAL, 50);
            case 40 -> new StreetTile(40, "Rathausstraße", 180, 56, StreetLevel.PREMIUM, 100);
        }
        return null;
    }
}
