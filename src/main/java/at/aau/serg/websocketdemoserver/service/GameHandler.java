package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.cards.RiskCardDeck;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameHandler {

    private final GameState gameState;
    private final TileActionHandler tileActionHandler;
    private final Dice dice;
    private final List<GameMessage> extraMessages = new ArrayList<>();

    public GameHandler() {
        this.gameState = new GameState();
        this.tileActionHandler = new TileActionHandler(
                new EventCardService(new BankCardDeck(), new RiskCardDeck())
        );
        this.dice = new Dice(1, 6);
    }

    public List<GameMessage> getExtraMessages() {
        return new ArrayList<>(extraMessages); // Kopie zurückgeben
    }

    public GameMessage handle(GameMessage message) {
        extraMessages.clear();

        if (message == null || message.getType() == null) {
            return MessageFactory.error("Ungültige oder fehlende Nachricht.");
        }

        return switch (message.getType()) {
            case ROLL_DICE -> handleRollDice(message.getPayload());
            case BUY_PROPERTY -> handleBuyProperty(message.getPayload());
            default -> MessageFactory.error("Unbekannter Nachrichtentyp: " + message.getType());
        };
    }

    public void initGame(List<PlayerDTO> players) {
        gameState.addPlayers(players);
        System.out.println("[INIT] Game started with " + players.size() + " players.");
    }

    public String getCurrentPlayerId() {
        Player current = gameState.getCurrentPlayer();
        return current != null ? String.valueOf(current.getId()) : null;
    }

    private GameMessage handleRollDice(Object payload) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return MessageFactory.error("Spieler nicht gefunden.");
            }

            if (player.getId() != gameState.getCurrentPlayer().getId()) {
                return MessageFactory.error("Nicht dein Zug!");
            }

            int diceRoll = dice.roll();
            int newIndex = (player.getCurrentTile() == null ? 0 : player.getCurrentTile().getIndex()) + diceRoll;
            newIndex = newIndex % gameState.getBoard().getTiles().size();
            player.moveToTile(newIndex);

            Tile tile = gameState.getBoard().getTile(newIndex);
            System.out.println("[ROLL] " + player.getNickname() + " rolled " + diceRoll + " → " + tile.getLabel());

            // Bewegung-Nachricht
            GameMessage moveMessage = MessageFactory.playerMoved(
                    player.getId(), newIndex, diceRoll, tile.getLabel(), tile.getClass().getSimpleName()
            );

            // Aktion auf dem neuen Feld
            GameMessage actionMessage = tileActionHandler.handleTileLanding(player, tile);
            extraMessages.add(actionMessage);

            // Spielerwechsel vorbereiten
            gameState.advanceTurn();
            Player nextPlayer = gameState.getCurrentPlayer();
            extraMessages.add(MessageFactory.currentPlayer(nextPlayer.getId()));

            return moveMessage;
        } catch (Exception e) {
            return MessageFactory.error("Fehler beim Würfeln: " + e.getMessage());
        }
    }

    private GameMessage handleBuyProperty(Object payload) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            int playerId = obj.getInt("playerId");
            int tilePos = obj.getInt("tilePos");

            Player player = gameState.getPlayer(playerId);

            if (player == null) {
                return MessageFactory.error("Spieler nicht gefunden.");
            }

            boolean success = player.purchaseStreet(tilePos);
            if (success) {
                Tile tile = gameState.getBoard().getTile(tilePos);
                System.out.println("[BUY] " + player.getNickname() + " bought " + tile.getLabel());
                return MessageFactory.propertyBought(playerId, tilePos, tile.getLabel());
            } else {
                return MessageFactory.error("Kauf fehlgeschlagen.");
            }
        } catch (Exception e) {
            return MessageFactory.error("Fehler beim Kaufen: " + e.getMessage());
        }
    }
}