package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import at.aau.serg.websocketdemoserver.service.TileActionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.util.List;

public class RollDiceRequest implements GameRequest {

    private final Dice dice;
    private final TileActionHandler tileActionHandler;
    private static final int MAX_ROUNDS = 15; // optionales Limit
    private static final boolean USE_ROUND_LIMIT = false; // aktivieren wenn gewünscht

    public RollDiceRequest(TileActionHandler tileActionHandler) {
        this.dice = new Dice(1, 6);
        this.tileActionHandler = tileActionHandler;
    }

    @Override
    public GameMessage handle(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            // Payload extrahieren
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);
            JSONObject obj = new JSONObject(json);

            int playerId = obj.getInt("playerId");
            Player player = gameState.getPlayer(playerId);

            if (player == null) {
                return MessageFactory.error(lobbyId, "Spieler nicht gefunden.");
            }

            if (player.getId() != gameState.getCurrentPlayerId()) {
                return MessageFactory.error(lobbyId, "Nicht dein Zug!");
            }

            // Würfeln und bewegen
            int diceRoll = dice.roll();
            int oldIndex = (player.getCurrentTile() != null) ? player.getCurrentTile().getIndex() : 0;
            int newIndex = (oldIndex + diceRoll) % gameState.getBoard().getTiles().size();
            player.moveToTile(newIndex);

            Tile tile = gameState.getBoard().getTile(newIndex);
            String tileName = tile.getLabel();
            String tileType = tile.getClass().getSimpleName();

            // Nachricht 1: Spieler bewegt sich
            GameMessage moveMessage = MessageFactory.playerMoved(
                    lobbyId, playerId, newIndex, diceRoll, tileName, tileType
            );

            // Nachricht 2: Aktion auf dem Feld
            GameMessage actionMessage = tileActionHandler.handleTileLanding(player, tile);
            actionMessage.setLobbyId(lobbyId);
            extraMessages.add(actionMessage);

            // Rundenlogik
            gameState.advanceTurn();

            /* Spielende prüfen (optional)
            if (USE_ROUND_LIMIT && gameState.isGameOver(MAX_ROUNDS, true)) {
                extraMessages.add(MessageFactory.gameOver(lobbyId, "Maximale Rundenanzahl erreicht."));
                return moveMessage;
            }
            
             */

            // Nachricht 3: Nächster Spieler ist am Zug
            Player next = gameState.getCurrentPlayer();
            extraMessages.add(MessageFactory.currentPlayer(lobbyId, next.getId()));

            return moveMessage;

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Würfeln: " + e.getMessage());
        }
    }
}
