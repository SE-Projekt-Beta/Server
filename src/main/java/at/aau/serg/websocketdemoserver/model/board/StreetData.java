package at.aau.serg.websocketdemoserver.model.board;

import java.util.List;

public class StreetData {

    private StreetData() {
        // prevent instantiation
    }

    public static final List<StreetDefinition> STREETS = List.of(
            new StreetDefinition(2, "Amtsplatz", 220, 80, StreetLevel.CHEAP, 160, 320),
            new StreetDefinition(5, "Murplatz", 300, 120, StreetLevel.NORMAL, 200, 400),
            new StreetDefinition(4,"Kraft- Zentrale", 400, 100, StreetLevel.NORMAL, 300, 500),
            new StreetDefinition(6, "Annenstraße", 250, 96, StreetLevel.NORMAL, 150, 300),
            new StreetDefinition(7, "Joanneumring", 220, 80, StreetLevel.NORMAL, 130, 260),
            new StreetDefinition(8, "Eisenbahn Wien- Graz", 180, 56, StreetLevel.PREMIUM, 100, 200),
            new StreetDefinition(10, "Joseph-Haydn-Gasse", 100, 24, StreetLevel.CHEAP, 50, 100),
            new StreetDefinition(12, "Schlossgrund", 220, 80, StreetLevel.CHEAP, 160, 320),
            new StreetDefinition(13, "Dampf-Schifffahrt", 300, 70, StreetLevel.PREMIUM, 400, 500),
            new StreetDefinition(14, "Seilabhn", 250, 96, StreetLevel.NORMAL, 150, 300),
            new StreetDefinition(15, "Kärntner Straße", 380, 200, StreetLevel.PREMIUM, 220, 440),
            new StreetDefinition(16, "Mariahilfer Straße", 350, 160, StreetLevel.PREMIUM, 220, 440),
            new StreetDefinition(17, "Kobenzlstraße", 250, 96, StreetLevel.NORMAL, 150, 300),
            new StreetDefinition(18, "Eisenbahn", 220, 80, StreetLevel.CHEAP, 160, 320),
            new StreetDefinition(19, "Landstraße", 300, 120, StreetLevel.NORMAL, 200, 400),
            new StreetDefinition(20, "Stifterstraße", 180, 56, StreetLevel.PREMIUM, 100, 200),
            new StreetDefinition(22, "Museumstraße", 220, 80, StreetLevel.CHEAP, 160, 320),
            new StreetDefinition(24, "Autobuslinie", 210, 72, StreetLevel.NORMAL, 120, 240),
            new StreetDefinition(25, "Mirabellplatz", 250, 96, StreetLevel.NORMAL, 150, 300),
            new StreetDefinition(26, "Westbahnstraße", 240, 88, StreetLevel.NORMAL, 140, 280),
            new StreetDefinition(27, "Universitätsplatz", 250, 96, StreetLevel.NORMAL, 150, 300),
            new StreetDefinition(29, "Burggasse", 140, 40, StreetLevel.NORMAL, 100, 200),
            new StreetDefinition(30, "Villacherstraße", 200, 64, StreetLevel.NORMAL, 110, 220),
            new StreetDefinition(32, "Alter Platz", 210, 72, StreetLevel.NORMAL, 120, 240),
            new StreetDefinition(34, "Flughafen Wien- Venedig", 300, 120, StreetLevel.NORMAL, 200, 400),
            new StreetDefinition(35, "Maria-Theresien-Straße", 300, 120, StreetLevel.NORMAL, 200, 400),
            new StreetDefinition(36, "Andreas-Hofer-Straße", 250, 96, StreetLevel.NORMAL, 150, 300),
            new StreetDefinition(37, "Boznerplatz", 300, 120, StreetLevel.NORMAL, 200, 400),
            new StreetDefinition(39, "Arlbergstraße", 120, 32, StreetLevel.NORMAL, 50, 100),
            new StreetDefinition(40, "Rathausstraße", 180, 56, StreetLevel.PREMIUM, 100, 200)
    );
}
