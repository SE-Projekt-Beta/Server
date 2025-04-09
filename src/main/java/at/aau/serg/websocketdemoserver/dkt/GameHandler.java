package at.aau.serg.websocketdemoserver.dkt;

import at.aau.serg.websocketdemoserver.dkt.lobby.Lobby;
import org.json.JSONArray;
import at.aau.serg.websocketdemoserver.messaging.dtos.EventCard;
import at.aau.serg.websocketdemoserver.messaging.dtos.EventCardBank;
import at.aau.serg.websocketdemoserver.messaging.dtos.EventCardRisiko;
import org.json.JSONObject;

import java.util.*;

public class GameHandler {

    private final GameState gameState = new GameState();
    private final GameBoard board = new GameBoard();
    private final List<GameMessage> extraMessages = new ArrayList<>();
    private final Map<Integer, String> ownership = new HashMap<>(); // Besitzverwaltung
    private final EventCardService eventCardService = new EventCardService();
    private final Lobby lobby = new Lobby();

    private Random random = new Random();



    public List<GameMessage> getExtraMessages() {
        return extraMessages;
    }

    public GameState getGameState() {
        return gameState;
    }

    private final List<EventCardRisiko> eventCardsRisiko = List.of(
            new EventCardRisiko("Gehe 3 Felder zurück", -3),
            new EventCardRisiko("Gehe 2 Felder vor", 2),
            new EventCardRisiko("Gehe 4 Felder zurück", -4),
            new EventCardRisiko("Gehe 4 Felder vor", 4)
    );

    private final List<EventCardBank> eventCardsBank = List.of(
            new EventCardBank("Für Unfallversicherung bezahlst du 200,-", -200),
            new EventCardBank("Für eine Autoreparatur bezahlst du 140,-", -140),
            new EventCardBank("Für die Auswertung einer Erfindung erhältst du 140,- aus öffentlichen Mitteln", 140),
            new EventCardBank("Die Bank zahlt dir an Dividenden 60,-", 60)
    );
    public String getOwner(int tilePos) {
        return ownership.get(tilePos);
    }

    public GameMessage handle(GameMessage msg) {
        switch (msg.getType()) {
            case "roll_dice":
                return handleRollDice(msg.getPayload());
            case "buy_property":
                return handleBuyProperty(msg.getPayload());
            case "join_lobby":
                return handleJoinLobby(msg.getPayload());
            case "start_game":
                return handleStartGame();
            default:
                return new GameMessage("error", "Unbekannter Typ: " + msg.getType());
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

            JSONObject movePayload = new JSONObject();
            movePayload.put("playerId", playerId);
            movePayload.put("pos", newPos);
            movePayload.put("dice", dice);
            movePayload.put("tileName", tile.getName());
            movePayload.put("tileType", tile.getTileType());

            System.out.println("Server: " + playerId + " ist auf " + tile.getName() + " (" + tile.getTileType() + ")");

            // Aktion ermitteln
            GameMessage actionMsg = decideAction(playerId, tile);

            // Aktion zur Extraschlange hinzufügen
            extraMessages.clear();
            extraMessages.add(actionMsg);

            return new GameMessage("player_moved", movePayload.toString());

        } catch (Exception e) {
            return new GameMessage("error", "Fehler: " + e.getMessage());
        }
    }

    private GameMessage handleBuyProperty(String payload) {
        try {
            JSONObject obj = new JSONObject(payload);
            String playerId = obj.getString("playerId");
            int tilePos = obj.getInt("tilePos");

            if (ownership.containsKey(tilePos)) {
                return new GameMessage("error", "Feld gehört schon jemandem!");
            }

            ownership.put(tilePos, playerId);
            System.out.println("Besitz gespeichert: " + playerId + " → Feld " + tilePos);

            Tile tile = board.getTileAt(tilePos);
            JSONObject response = new JSONObject();
            response.put("playerId", playerId);
            response.put("tilePos", tilePos);
            response.put("tileName", tile.getName());

            return new GameMessage("property_bought", response.toString());

        } catch (Exception e) {
            return new GameMessage("error", "Fehler beim Kauf: " + e.getMessage());
        }
    }


    GameMessage decideAction(String playerId, Tile tile) {
        JSONObject payload = new JSONObject();
        payload.put("playerId", playerId);
        payload.put("tilePos", tile.getPosition());
        payload.put("tileName", tile.getName());

        switch (tile.getTileType()) {
            case "street":
            case "station":
                String owner = ownership.get(tile.getPosition());
                if (owner != null && !owner.equals(playerId)) {
                    payload.put("ownerId", owner);
                    return new GameMessage("must_pay_rent", payload.toString());
                }
                return new GameMessage("can_buy_property", payload.toString());

            case "tax":
                return new GameMessage("pay_tax", payload.toString());
            case "event":
                String card = eventCardService.drawCard();
                return new GameMessage("event_card", card);
            case "gotojail":
                return new GameMessage("go_to_jail", payload.toString());

            default:
                return new GameMessage("skipped", payload.toString());
        }
    }

    private GameMessage handleJoinLobby(String payload) {
        try {
            String playerName = lobby.addPlayer();
            System.out.println("Neuer Spieler beigetreten: " + playerName);

            // Antwort an alle Spieler
            return new GameMessage("lobby_update", lobby.toJson().toString());

        } catch (Exception e) {
            return new GameMessage("error", "Fehler beim Lobby-Beitritt: " + e.getMessage());
        }
    }

    private GameMessage handleStartGame() {
        System.out.println("Spiel startet!");

        // Schicke an ALLE Clients die Nachricht "start_game"
        extraMessages.clear();
        extraMessages.add(new GameMessage("start_game", ""));

        // return dummy (wird eh nicht verwendet direkt)
        return new GameMessage("info", "Startsignal gesendet");
    }





}