package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerMovePayload;
import at.aau.serg.websocketdemoserver.dto.PropertyBoughtPayload;
import at.aau.serg.websocketdemoserver.model.GameState;
import at.aau.serg.websocketdemoserver.model.tiles.EventCardBank;
import at.aau.serg.websocketdemoserver.model.tiles.EventCardRisiko;
import at.aau.serg.websocketdemoserver.model.GameBoard;
import at.aau.serg.websocketdemoserver.model.Tile;
import org.json.JSONObject;

import java.util.*;


public class GameHandler {

    private final GameState gameState = new GameState();
    private final GameBoard board = new GameBoard();
    private final List<GameMessage> extraMessages = new ArrayList<>();
    private final Map<Integer, String> ownership = new HashMap<>();
    private final EventCardService eventCardService = new EventCardService();
    private final Random random = new Random();

    public List<GameMessage> getExtraMessages() {
        return extraMessages;
    }

    public GameState getGameState() {
        return gameState;
    }

    public String getOwner(int tilePos) {
        return ownership.get(tilePos);
    }

    /**
     * Hauptmethode, die eingehende GameMessages behandelt
     */
    public GameMessage handle(GameMessage msg) {
        switch (msg.getType()) {
            case ROLL_DICE:
                return handleRollDice((String) msg.getPayload());
            case BUY_PROPERTY:
                return handleBuyProperty((String) msg.getPayload());
            default:
                return new GameMessage(MessageType.ERROR, "Unbekannter Nachrichtentyp: " + msg.getType());
        }
    }

    /**
     * Würfelt und bewegt einen Spieler
     */
    private GameMessage handleRollDice(String payload) {
        try {
            org.json.JSONObject obj = new org.json.JSONObject(payload);
            String playerId = obj.getString("playerId");

            int dice = random.nextInt(6) + 1;
            int currentPos = gameState.getPosition(playerId);
            int newPos = (currentPos + dice) % 40;
            gameState.updatePosition(playerId, newPos);

            Tile tile = board.getTileAt(newPos);

            // Payload-Objekt erstellen
            PlayerMovePayload movePayload = new PlayerMovePayload(
                    playerId, newPos, dice, tile.getName(), tile.getTileType()
            );

            System.out.println("Server: " + playerId + " ist auf " + tile.getName() + " (" + tile.getTileType() + ")");

            // Aktion auf dem Tile ermitteln
            GameMessage actionMsg = decideAction(playerId, tile);

            // Extraschlange aktualisieren
            extraMessages.clear();
            extraMessages.add(actionMsg);

            return new GameMessage(MessageType.PLAYER_MOVED, movePayload);

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Würfeln: " + e.getMessage());
        }
    }

    /**
     * Spieler kauft ein Feld
     */
    private GameMessage handleBuyProperty(String payload) {
        try {
            JSONObject obj = new JSONObject(payload);
            String playerId = obj.getString("playerId");
            int tilePos = obj.getInt("tilePos");

            if (ownership.containsKey(tilePos)) {
                return new GameMessage(MessageType.ERROR, "Feld gehört bereits jemandem!");
            }

            ownership.put(tilePos, playerId);
            System.out.println("Besitz gespeichert: " + playerId + " → Feld " + tilePos);

            Tile tile = board.getTileAt(tilePos);

            PropertyBoughtPayload boughtPayload = new PropertyBoughtPayload(
                    playerId, tilePos, tile.getName()
            );

            return new GameMessage(MessageType.PROPERTY_BOUGHT, boughtPayload);

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Kauf: " + e.getMessage());
        }
    }

    /**
     * Entscheidet basierend auf Tile-Typ, was passieren muss
     */
    private GameMessage decideAction(String playerId, Tile tile) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", playerId);
        payload.put("tilePos", tile.getPosition());
        payload.put("tileName", tile.getName());

        switch (tile.getTileType()) {
            case "street":
            case "station":
                String owner = ownership.get(tile.getPosition());
                if (owner != null && !owner.equals(playerId)) {
                    payload.put("ownerId", owner);
                    return new GameMessage(MessageType.MUST_PAY_RENT, payload);
                }
                return new GameMessage(MessageType.CAN_BUY_PROPERTY, payload);

            case "tax":
                return new GameMessage(MessageType.PAY_TAX, payload);

            case "event_risiko":
                EventCardRisiko risikoCard = eventCardService.drawRisikoCard();
                payload.put("eventTitle", risikoCard.getTitle());
                payload.put("eventDescription", risikoCard.getDescription());
                payload.put("eventAmount", risikoCard.getAmount());
                payload.put("eventType", "risiko");
                return new GameMessage(MessageType.DRAW_EVENT_RISIKO_CARD, payload);

            case "event_bank":
                EventCardBank bankCard = eventCardService.drawBankCard();
                payload.put("eventTitle", bankCard.getTitle());
                payload.put("eventDescription", bankCard.getDescription());
                payload.put("eventAmount", bankCard.getAmount());
                payload.put("eventType", "bank");
                return new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, payload);

            case "goto_jail":
                return new GameMessage(MessageType.GO_TO_JAIL, payload);

            default:
                return new GameMessage(MessageType.SKIPPED, payload);
        }
    }
}

