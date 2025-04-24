package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.GameState;
import at.aau.serg.websocketdemoserver.model.Player;
import at.aau.serg.websocketdemoserver.model.tiles.*;
import at.aau.serg.websocketdemoserver.model.GameBoard;
import at.aau.serg.websocketdemoserver.model.Tile;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.util.*;



@Service
public class GameHandler {

    private final GameState gameState = new GameState();
    private final GameBoard board = new GameBoard();
    private final Map<Integer, String> ownership = new HashMap<>();
    private final EventCardService eventCardService = new EventCardService();
    private final Random random = new Random();

    private final List<GameMessage> extraMessages = new ArrayList<>();

    public List<GameMessage> getExtraMessages() {
        return extraMessages;
    }

    public GameMessage handle(GameMessage message) {
        extraMessages.clear(); // immer zuerst leeren!

        switch (message.getType()) {
            case ROLL_DICE:
                return handleRollDice(message.getPayload());
            case BUY_PROPERTY:
                return handleBuyProperty(message.getPayload());
            default:
                return new GameMessage(MessageType.ERROR, "Unbekannte Nachricht: " + message.getType());
        }
    }

    private GameMessage handleRollDice(Object payload) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            String playerId = obj.getString("playerId");

            int dice = random.nextInt(6) + 1;
            int oldPos = gameState.getPosition(playerId);
            int newPos = (oldPos + dice) % 40;
            gameState.updatePosition(playerId, newPos);

            Tile tile = board.getTileAt(newPos);

            // Spielerbewegung schicken
            PlayerMovePayload movePayload = new PlayerMovePayload(
                    playerId, newPos, dice, tile.getName(), tile.getTileType()
            );
            GameMessage moveMessage = new GameMessage(MessageType.PLAYER_MOVED, movePayload);

            // Entscheidung was als nächstes passiert (Extra-Nachricht)
            GameMessage actionMessage = decideAction(playerId, tile);
            extraMessages.add(actionMessage);

            return moveMessage;
        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Würfeln: " + e.getMessage());
        }
    }

    private GameMessage handleBuyProperty(Object payload) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            String playerId = obj.getString("playerId");
            int tilePos = obj.getInt("tilePos");

            if (ownership.containsKey(tilePos)) {
                return new GameMessage(MessageType.ERROR, "Feld gehört schon jemandem!");
            }

            ownership.put(tilePos, playerId);
            Tile tile = board.getTileAt(tilePos);

            PropertyBoughtPayload boughtPayload = new PropertyBoughtPayload(
                    playerId, tilePos, tile.getName()
            );
            return new GameMessage(MessageType.PROPERTY_BOUGHT, boughtPayload);
        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Kaufen: " + e.getMessage());
        }
    }

    public GameMessage decideAction(String playerId, Tile tile) {
        if (tile instanceof Event) {
            String type = tile.getTileType(); // event_bank oder event_risiko

            if (type.equals("event_bank")) {
                EventCardBank bankCard = eventCardService.drawBankCard();
                EventCardPayload payload = new EventCardPayload(
                        bankCard.getTitle(),
                        bankCard.getDescription(),
                        bankCard.getAmount(),
                        "bank"
                );
                return new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, payload);
            } else if (type.equals("event_risiko")) {
                EventCardRisiko risikoCard = eventCardService.drawRisikoCard();
                EventCardPayload payload = new EventCardPayload(
                        risikoCard.getTitle(),
                        risikoCard.getDescription(),
                        risikoCard.getAmount(),
                        "risiko"
                );
                return new GameMessage(MessageType.DRAW_EVENT_RISIKO_CARD, payload);
            } else {
                // normales Ereignisfeld → einfach überspringen
                return createSkippedMessage(playerId, tile);
            }
        }

        if (tile instanceof GoToJail) {
            Map<String, Object> jailPayload = new HashMap<>();
            jailPayload.put("playerId", playerId);
            jailPayload.put("tilePos", tile.getPosition());
            jailPayload.put("tileName", tile.getName());
            return new GameMessage(MessageType.GO_TO_JAIL, jailPayload);
        }

        if (tile instanceof Free || tile instanceof Start || tile instanceof Jail) {
            // Frei Parken, Start, Gefängnis (Besuch) → nichts tun
            return createSkippedMessage(playerId, tile);
        }

        if (tile instanceof Tax) {
            Map<String, Object> taxPayload = new HashMap<>();
            taxPayload.put("playerId", playerId);
            taxPayload.put("tilePos", tile.getPosition());
            taxPayload.put("tileName", tile.getName());
            return new GameMessage(MessageType.PAY_TAX, taxPayload);
        }

        // Standard-Fälle: Street, Station (Straßen und Bahnhöfe)
        if (tile instanceof Street || tile instanceof Station) {
            String owner = ownership.get(tile.getPosition());
            if (owner != null && !owner.equals(playerId)) {
                Map<String, Object> rentPayload = new HashMap<>();
                rentPayload.put("playerId", playerId);
                rentPayload.put("tilePos", tile.getPosition());
                rentPayload.put("tileName", tile.getName());
                rentPayload.put("ownerId", owner);
                return new GameMessage(MessageType.MUST_PAY_RENT, rentPayload);
            } else {
                Map<String, Object> buyPayload = new HashMap<>();
                buyPayload.put("playerId", playerId);
                buyPayload.put("tilePos", tile.getPosition());
                buyPayload.put("tileName", tile.getName());
                return new GameMessage(MessageType.CAN_BUY_PROPERTY, buyPayload);
            }
        }

        // Fallback: unbekanntes Feld → SKIPPED
        return createSkippedMessage(playerId, tile);
    }
    private GameMessage createSkippedMessage(String playerId, Tile tile) {
        Map<String, Object> skippedPayload = new HashMap<>();
        skippedPayload.put("playerId", playerId);
        skippedPayload.put("tilePos", tile.getPosition());
        skippedPayload.put("tileName", tile.getName());
        return new GameMessage(MessageType.SKIPPED, skippedPayload);
    }

    public void initGame(List<Player> players) {
        List<String> ids = players.stream().map(Player::getId).toList();
        gameState.setPlayerOrder(ids);
    }



}

