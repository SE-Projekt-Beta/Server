// src/main/java/at/aau/serg/websocketdemoserver/service/GameHandler.java
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
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all in-game messages, now routing every GameMessage through its lobbyId.
 */
@Service
public class GameHandler {

    private final GameState gameState;
    private final TileActionHandler tileActionHandler;
    private final Dice dice;
    private final List<GameMessage> extraMessages = new ArrayList<>();

    public GameHandler() {
        this.gameState = new GameState();
        this.tileActionHandler = new TileActionHandler(
                new EventCardService(BankCardDeck.get(), RiskCardDeck.get())
        );
        this.dice = new Dice(1, 6);
    }

    /**
     * Returns any additional messages generated during the last handle(...) call.
     */
    public List<GameMessage> getExtraMessages() {
        return new ArrayList<>(extraMessages);
    }

    /**
     * Main entry point for game messages.
     * Extracts lobbyId and dispatches to the correct handler.
     */
    public GameMessage handle(GameMessage message) {
        extraMessages.clear();
        int lobbyId = message.getLobbyId();

        if (message == null || message.getType() == null) {
            return MessageFactory.error(lobbyId, "Ungültige oder fehlende Nachricht.");
        }

        return switch (message.getType()) {
            case ROLL_DICE     -> handleRollDice(lobbyId, message.getPayload());
            case BUY_PROPERTY  -> handleBuyProperty(lobbyId, message.getPayload());
            default             -> MessageFactory.error(lobbyId,
                    "Unbekannter Nachrichtentyp: " + message.getType());
        };
    }

    /**
     * Initialize the game state with the given players.
     */
    public void initGame(List<PlayerDTO> players) {
        List<Player> playerModels = players.stream()
                .map(dto -> new Player(dto.getId(), dto.getNickname(), gameState.getBoard()))
                .collect(Collectors.toList());

        gameState.startGame(playerModels);
        System.out.println("Game initialized with players: " + playerModels);
    }


    public String getCurrentPlayerId() {
        Player current = gameState.getCurrentPlayer();
        return current != null ? String.valueOf(current.getId()) : null;
    }

    /**
     * Handles a ROLL_DICE message for a specific lobby.
     */
    private GameMessage handleRollDice(int lobbyId, Object payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(payload);  // this converts {playerId=fdghfdh} to {"playerId":"fdghfdh"}

            JSONObject obj = new JSONObject(jsonPayload);  // now it's valid
            System.out.println("[ROLL] Parsed JSON: " + obj.toString(2));
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return MessageFactory.error(lobbyId, "Spieler nicht gefunden.");
            }
            if (player.getId() != gameState.getCurrentPlayer().getId()) {
                return MessageFactory.error(lobbyId, "Nicht dein Zug!");
            }

            int diceRoll = dice.roll();
            int newIndex = (player.getCurrentTile() == null ? 0 : player.getCurrentTile().getIndex()) + diceRoll;
            newIndex %= gameState.getBoard().getTiles().size();
            player.moveToTile(newIndex);

            Tile tile = gameState.getBoard().getTile(newIndex);
            String tileName = tile.getLabel();
            String tileType = tile.getClass().getSimpleName();
            System.out.println("[ROLL] " + player.getNickname() + " rolled " + diceRoll + " → " + tileName);

            // 1) Movement message scoped to this lobby
            GameMessage moveMessage = MessageFactory.playerMoved(
                    lobbyId,
                    player.getId(),
                    newIndex,
                    diceRoll,
                    tileName,
                    tileType
            );

            // 2) Action message from landing on the tile
            GameMessage actionMessage = tileActionHandler.handleTileLanding(player, tile);
            actionMessage.setLobbyId(lobbyId);
            extraMessages.add(actionMessage);

            // 3) Advance turn and notify next player
            gameState.advanceTurn();
            Player next = gameState.getCurrentPlayer();
            extraMessages.add(MessageFactory.currentPlayer(
                    lobbyId,
                    next.getId()
            ));

            return moveMessage;
        } catch (Exception e) {
            System.out.println("[ROLL] Error: " + e.getMessage());
            // which line is the error?
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Würfeln: " + e.getMessage());
        }
    }

    /**
     * Handles a BUY_PROPERTY message for a specific lobby.
     */
    private GameMessage handleBuyProperty(int lobbyId, Object payload) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            int playerId = obj.getInt("playerId");
            int tilePos  = obj.getInt("tilePos");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return MessageFactory.error(lobbyId, "Spieler nicht gefunden.");
            }

            boolean success = player.purchaseStreet(tilePos);
            if (success) {
                Tile tile = gameState.getBoard().getTile(tilePos);
                System.out.println("[BUY] " + player.getNickname() + " bought " + tile.getLabel());
                return MessageFactory.propertyBought(
                        lobbyId,
                        playerId,
                        tilePos,
                        tile.getLabel()
                );
            } else {
                return MessageFactory.error(lobbyId, "Kauf fehlgeschlagen.");
            }
        } catch (Exception e) {
            return MessageFactory.error(lobbyId, "Fehler beim Kaufen: " + e.getMessage());
        }
    }
    public GameHandler(GameState gameState) {
        this.gameState = gameState;
        this.tileActionHandler = new TileActionHandler(
                new EventCardService(BankCardDeck.get(), RiskCardDeck.get())
        );
        this.dice = new Dice(1, 6);
    }

}
