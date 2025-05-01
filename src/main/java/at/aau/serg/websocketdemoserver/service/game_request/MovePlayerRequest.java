package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MovePlayerRequest implements GameHandlerInterface {

    private static final int BOARD_SIZE = 41; // Position 0 wird übersprungen
    private static final int START_BONUS = 200;
    private static final int JAIL_INDEX = 10;

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");
            int steps = obj.getInt("steps");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return error("Spieler nicht gefunden.");
            }

            if (player.isSuspended()) {
                player.decreaseSuspension();
                return new GameMessage(MessageType.SKIPPED, Map.of(
                        "playerId", playerId,
                        "tilePos", player.getCurrentTile().getIndex(),
                        "tileName", player.getCurrentTile().getLabel()
                ));
            }

            int currentIndex = (player.getCurrentTile() == null) ? 1 : player.getCurrentTile().getIndex();
            int newIndex = (currentIndex + steps) % BOARD_SIZE;
            if (newIndex == 0) newIndex = 1;

            // Startfeld überquert?
            if (newIndex < currentIndex) {
                player.setCash(player.getCash() + START_BONUS);
            }

            player.moveToTile(newIndex);
            Tile destination = gameState.getBoard().getTile(newIndex);

            List<GameMessage> messages = new ArrayList<>();

            messages.add(new GameMessage(MessageType.PLAYER_MOVED, Map.of(
                    "playerId", playerId,
                    "pos", newIndex,
                    "dice", steps,
                    "tileName", destination.getLabel(),
                    "tileType", destination.getClass().getSimpleName()
            )));

            messages.addAll(handleTile(gameState, player, destination));

            // Spielerwechsel vorbereiten
            gameState.advanceTurn();
            Player next = gameState.getCurrentPlayer();

            messages.add(new GameMessage(MessageType.CURRENT_PLAYER, Map.of(
                    "playerId", next.getId()
            )));

            return new GameMessage(MessageType.PLAYER_MOVED, messages); // gepackte Liste als Payload (Client muss das können)
        } catch (Exception e) {
            return error("Fehler bei Spielerbewegung: " + e.getMessage());
        }
    }

    private List<GameMessage> handleTile(GameState gameState, Player player, Tile tile) {
        List<GameMessage> events = new ArrayList<>();

        if (tile instanceof GoToJailTile) {
            player.suspendForRounds(3);
            player.moveToTile(JAIL_INDEX);
            Tile jail = gameState.getBoard().getTile(JAIL_INDEX);
            events.add(new GameMessage(MessageType.GO_TO_JAIL, Map.of(
                    "playerId", player.getId(),
                    "tilePos", jail.getIndex(),
                    "tileName", jail.getLabel()
            )));
            return events;
        }

        if (tile instanceof StreetTile street) {
            if (street.getOwner() == null) {
                events.add(new GameMessage(MessageType.CAN_BUY_PROPERTY, Map.of(
                        "playerId", player.getId(),
                        "tilePos", street.getIndex(),
                        "tileName", street.getLabel(),
                        "price", street.getPrice()
                )));
            } else if (street.getOwner().getId() != player.getId()) {
                int rent = street.calculateRent();
                player.transferCash(street.getOwner(), rent);
                events.add(new GameMessage(MessageType.MUST_PAY_RENT, Map.of(
                        "playerId", player.getId(),
                        "ownerId", street.getOwner().getId(),
                        "tilePos", street.getIndex(),
                        "tileName", street.getLabel(),
                        "amount", rent
                )));
            }
        }

        if (tile instanceof RiskTile) {
            events.add(new GameMessage(MessageType.DRAW_EVENT_RISIKO_CARD, Map.of(
                    "title", "Risiko",
                    "description", "Ziehe eine Risikokarte." // Platzhalter
            )));
        }

        if (tile instanceof BankTile) {
            events.add(new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, Map.of(
                    "title", "Bank",
                    "description", "Ziehe eine Bankkarte." // Platzhalter
            )));
        }

        return events;
    }

    private GameMessage error(String text) {
        return new GameMessage(MessageType.ERROR, text);
    }
}
