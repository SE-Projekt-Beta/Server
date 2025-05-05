package at.aau.serg.websocketdemoserver.model.board;

public record StreetDefinition(
        int index,
        String name,
        int price,
        int baseRent,
        StreetLevel level,
        int houseCost,
        int hotelCost
) {}

