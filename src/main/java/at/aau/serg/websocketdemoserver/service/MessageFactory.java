package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerMovePayload;
import at.aau.serg.websocketdemoserver.dto.PropertyBoughtPayload;

import java.util.Map;

/**
 * Utility class for building GameMessage instances.
 * Now includes lobbyId on every message for multi-lobby routing.
 */
public class MessageFactory {

    private MessageFactory() {
        // Utility class – no instances
    }

    /**
     * Notify that a player moved.
     */
    public static GameMessage playerMoved(int lobbyId, int playerId, int newPos, int diceRoll, String tileName, String tileType) {
        return new GameMessage(
                lobbyId,
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

    /**
     * Notify whose turn it is.
     */
    public static GameMessage currentPlayer(int lobbyId, int playerId) {
        return new GameMessage(
                lobbyId,
                MessageType.CURRENT_PLAYER,
                Map.of("playerId", String.valueOf(playerId))
        );
    }

    /**
     * Notify that a property was bought.
     */
    public static GameMessage propertyBought(int lobbyId, int playerId, int tilePos, String tileName) {
        return new GameMessage(
                lobbyId,
                MessageType.PROPERTY_BOUGHT,
                new PropertyBoughtPayload(
                        String.valueOf(playerId),
                        tilePos,
                        tileName
                )
        );
    }

    /**
     * Offer the player the chance to buy a property.
     */
    public static GameMessage canBuyProperty(int lobbyId, int playerId, int tilePos, String tileName, int price) {
        return new GameMessage(
                lobbyId,
                MessageType.CAN_BUY_PROPERTY,
                Map.of(
                        "playerId", playerId,
                        "tilePos", tilePos,
                        "tileName", tileName,
                        "price", price
                )
        );
    }

    /**
     * Notify that rent must be paid.
     */
    public static GameMessage mustPayRent(int lobbyId, int playerId, int ownerId, int tilePos, String tileName, int rent) {
        return new GameMessage(
                lobbyId,
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

    /**
     * Draw an event card (bank or risiko).
     */
    public static GameMessage drawEventCard(int lobbyId, String eventType, String title, String description) {
        MessageType type = switch (eventType) {
            case "bank"   -> MessageType.DRAW_EVENT_BANK_CARD;
            case "risiko"-> MessageType.DRAW_EVENT_RISIKO_CARD;
            default       -> MessageType.ERROR;
        };
        return new GameMessage(
                lobbyId,
                type,
                Map.of(
                        "title", title,
                        "description", description
                )
        );
    }

    /**
     * Send the player to jail.
     */
    public static GameMessage goToJail(int lobbyId, int playerId) {
        return new GameMessage(
                lobbyId,
                MessageType.GO_TO_JAIL,
                Map.of(
                        "playerId", playerId,
                        "tilePos", 10,       // assumed jail position
                        "tileName", "Gefängnis"
                )
        );
    }

    /**
     * Notify that the player skipped their turn.
     */
    public static GameMessage skippedTurn(int lobbyId, int playerId, int tilePos, String tileName) {
        return new GameMessage(
                lobbyId,
                MessageType.SKIPPED,
                Map.of(
                        "playerId", playerId,
                        "tilePos", tilePos,
                        "tileName", tileName
                )
        );
    }

    /**
     * Send an error message.
     */
    public static GameMessage error(int lobbyId, String errorMessage) {
        return new GameMessage(
                lobbyId,
                MessageType.ERROR,
                errorMessage
        );
    }
}