package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import java.util.Map;

public class MessageFactory {

    private MessageFactory() {
        // Utility Class → kein Konstruktor
    }

    public static GameMessage playerMoved(int playerId, int newPos, int diceRoll, String tileName, String tileType) {
        return new GameMessage(
                MessageType.PLAYER_MOVED,
                new PlayerMovePayload(
                        String.valueOf(playerId),
                        newPos,
                        diceRoll,
                        tileName,
                        tileType
                )
        );
    }

    public static GameMessage currentPlayer(int playerId) {
        return new GameMessage(
                MessageType.CURRENT_PLAYER,
                Map.of("playerId", String.valueOf(playerId))
        );
    }

    public static GameMessage propertyBought(int playerId, int tilePos, String tileName) {
        return new GameMessage(
                MessageType.PROPERTY_BOUGHT,
                new PropertyBoughtPayload(
                        String.valueOf(playerId),
                        tilePos,
                        tileName
                )
        );
    }

    public static GameMessage canBuyProperty(int playerId, int tilePos, String tileName, int price) {
        return new GameMessage(
                MessageType.CAN_BUY_PROPERTY,
                Map.of(
                        "playerId", playerId,
                        "tilePos", tilePos,
                        "tileName", tileName,
                        "price", price
                )
        );
    }

    public static GameMessage mustPayRent(int playerId, int ownerId, int tilePos, String tileName, int rent) {
        return new GameMessage(
                MessageType.MUST_PAY_RENT,
                Map.of(
                        "playerId", playerId,
                        "ownerId", ownerId,
                        "tilePos", tilePos,
                        "tileName", tileName,
                        "amount", rent
                )
        );
    }

    public static GameMessage drawEventCard(String eventType, String title, String description) {
        MessageType type = switch (eventType) {
            case "bank" -> MessageType.DRAW_EVENT_BANK_CARD;
            case "risiko" -> MessageType.DRAW_EVENT_RISIKO_CARD;
            default -> MessageType.ERROR;
        };

        return new GameMessage(
                type,
                Map.of(
                        "title", title,
                        "description", description
                )
        );
    }

    public static GameMessage goToJail(int playerId) {
        return new GameMessage(
                MessageType.GO_TO_JAIL,
                Map.of(
                        "playerId", playerId,
                        "tilePos", 10, // Annahme: Gefängnisposition 10
                        "tileName", "Gefängnis"
                )
        );
    }

    public static GameMessage skippedTurn(int playerId, int tilePos, String tileName) {
        return new GameMessage(
                MessageType.SKIPPED,
                Map.of(
                        "playerId", playerId,
                        "tilePos", tilePos,
                        "tileName", tileName
                )
        );
    }

    public static GameMessage error(String errorMessage) {
        return new GameMessage(
                MessageType.ERROR,
                errorMessage
        );
    }
}
