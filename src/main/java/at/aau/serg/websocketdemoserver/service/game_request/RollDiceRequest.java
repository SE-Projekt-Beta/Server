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

    public RollDiceRequest(TileActionHandler tileActionHandler) {
        this.dice = new Dice(1, 6);
        this.tileActionHandler = tileActionHandler;
    }

    @Override
    public GameMessage handle(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            // Payload verarbeiten
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);
            JSONObject obj = new JSONObject(json);

            int playerId = obj.getInt("playerId");
            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return MessageFactory.error(lobbyId, "Spieler nicht gefunden.");
            }

            if (player.getId() != gameState.getCurrentPlayer().getId()) {
                return MessageFactory.error(lobbyId, "Nicht dein Zug!");
            }

            int diceRoll = dice.roll();
            int oldIndex = (player.getCurrentTile() != null) ? player.getCurrentTile().getIndex() : 0;
            int newIndex = (oldIndex + diceRoll) % gameState.getBoard().getTiles().size();
            player.moveToTile(newIndex);

            Tile tile = gameState.getBoard().getTile(newIndex);
            String tileName = tile.getLabel();
            String tileType = tile.getClass().getSimpleName();

            // Nachricht 1: Bewegung
            GameMessage moveMessage = MessageFactory.playerMoved(
                    lobbyId, playerId, newIndex, diceRoll, tileName, tileType
            );

            // Nachricht 2: Feldaktion
            GameMessage actionMessage = tileActionHandler.handleTileLanding(player, tile);
            actionMessage.setLobbyId(lobbyId);
            extraMessages.add(actionMessage);

            // Nachricht 3: Zugweitergabe
            gameState.advanceTurn();
            Player next = gameState.getCurrentPlayer();
            extraMessages.add(MessageFactory.currentPlayer(lobbyId, next.getId()));

            return moveMessage;

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Würfeln: " + e.getMessage());
        }
    }
}
