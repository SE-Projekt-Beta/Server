package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.GameState;
import at.aau.serg.websocketdemoserver.model.tiles.EventCardBank;
import at.aau.serg.websocketdemoserver.model.tiles.EventCardRisiko;
import at.aau.serg.websocketdemoserver.model.GameBoard;
import at.aau.serg.websocketdemoserver.model.Tile;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.util.*;



@Service
public class GameHandler {

    private final GameState gameState = new GameState();
    private final GameBoard board = new GameBoard();
    private final List<GameMessage> extraMessages = new ArrayList<>();
    private final Map<Integer, String> ownership = new HashMap<>(); // Besitzverwaltung
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

    public GameMessage handle(GameMessage msg) {
        switch (msg.getType()) {
            case ROLL_DICE:
                return handleRollDice((String) msg.getPayload());
            case BUY_PROPERTY:
                return handleBuyProperty((BuyPropertyPayload) msg.getPayload());
            default:
                return new GameMessage(MessageType.ERROR, "Unbekannter Nachrichtentyp: " + msg.getType());
        }
    }

    private GameMessage handleRollDice(String payload) {
        try {
            JSONObject obj = new JSONObject(payload);
            String playerId = obj.getString("playerId");

            int dice = random.nextInt(6) + 1;
            int currentPos = gameState.getPosition(playerId);
            int newPos = (currentPos + dice) % 40;
            gameState.updatePosition(playerId, newPos);

            Tile tile = board.getTileAt(newPos);

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

    private GameMessage handleBuyProperty(BuyPropertyPayload payload) {
        try {
            String playerId = payload.getPlayerId();
            int tilePos = payload.getTilePos();

            // Überprüfen, ob das Feld schon jemandem gehört
            if (ownership.containsKey(tilePos)) {
                return new GameMessage(MessageType.ERROR, "Feld gehört bereits jemandem!");
            }

            // Besitz speichern
            ownership.put(tilePos, playerId);
            System.out.println("Besitz gespeichert: " + playerId + " → Feld " + tilePos);

            // Spielfeld holen
            Tile tile = board.getTileAt(tilePos);

            // Antwort-Payload vorbereiten
            PropertyBoughtPayload responsePayload = new PropertyBoughtPayload(
                    playerId,
                    tilePos,
                    tile.getName()
            );

            return new GameMessage(MessageType.PROPERTY_BOUGHT, responsePayload);

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Kauf: " + e.getMessage());
        }
    }

    public GameMessage decideAction(String playerId, Tile tile) {
        switch (tile.getTileType()) {
            case "street":
            case "station":
                String owner = ownership.get(tile.getPosition());
                if (owner != null && !owner.equals(playerId)) {
                    Map<String, Object> rentPayload = new HashMap<>();
                    rentPayload.put("playerId", playerId);
                    rentPayload.put("tilePos", tile.getPosition());
                    rentPayload.put("tileName", tile.getName());
                    rentPayload.put("ownerId", owner);
                    return new GameMessage(MessageType.MUST_PAY_RENT, rentPayload);
                }
                Map<String, Object> buyPayload = new HashMap<>();
                buyPayload.put("playerId", playerId);
                buyPayload.put("tilePos", tile.getPosition());
                buyPayload.put("tileName", tile.getName());
                return new GameMessage(MessageType.CAN_BUY_PROPERTY, buyPayload);

            case "tax":
                Map<String, Object> taxPayload = new HashMap<>();
                taxPayload.put("playerId", playerId);
                taxPayload.put("tilePos", tile.getPosition());
                taxPayload.put("tileName", tile.getName());
                return new GameMessage(MessageType.PAY_TAX, taxPayload);

            case "event_risiko":
                EventCardRisiko risikoCard = eventCardService.drawRisikoCard();
                EventCardPayload risikoPayload = new EventCardPayload(
                        risikoCard.getTitle(),
                        risikoCard.getDescription(),
                        risikoCard.getAmount(),
                        "risiko"
                );
                return new GameMessage(MessageType.DRAW_EVENT_RISIKO_CARD, risikoPayload);

            case "event_bank":
                EventCardBank bankCard = eventCardService.drawBankCard();
                EventCardPayload bankPayload = new EventCardPayload(
                        bankCard.getTitle(),
                        bankCard.getDescription(),
                        bankCard.getAmount(),
                        "bank"
                );
                return new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, bankPayload);

            case "goto_jail":
                Map<String, Object> jailPayload = new HashMap<>();
                jailPayload.put("playerId", playerId);
                jailPayload.put("tilePos", tile.getPosition());
                jailPayload.put("tileName", tile.getName());
                return new GameMessage(MessageType.GO_TO_JAIL, jailPayload);

            default:
                Map<String, Object> skippedPayload = new HashMap<>();
                skippedPayload.put("playerId", playerId);
                skippedPayload.put("tilePos", tile.getPosition());
                skippedPayload.put("tileName", tile.getName());
                return new GameMessage(MessageType.SKIPPED, skippedPayload);
        }
    }
}

