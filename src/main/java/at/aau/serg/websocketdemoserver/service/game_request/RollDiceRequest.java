package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.util.Dice;

import java.util.Map;

public class RollDiceRequest implements GameHandlerInterface {

    private final Dice dice = new Dice(1, 6); // Würfel mit Standardbereich

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            Player current = gameState.getCurrentPlayer();
            if (current == null || current.getId() != playerId) {
                return new GameMessage(MessageType.ERROR, "Nicht dein Zug!");
            }

            if (player.isSuspended()) {
                int roundsLeft = player.getSuspensionRounds();
                player.decreaseSuspension();
                return new GameMessage(MessageType.SKIPPED, Map.of(
                        "playerId", player.getId(),
                        "tilePos", player.getCurrentTile().getIndex(),
                        "tileName", player.getCurrentTile().getLabel(),
                        "suspension", roundsLeft
                ));
            }

            int roll = dice.roll();
            int oldIndex = player.getCurrentTile() != null ? player.getCurrentTile().getIndex() : 0;
            int newIndex = (oldIndex + roll) % gameState.getBoard().getTiles().size();

            player.moveToTile(newIndex);
            Tile landedTile = player.getCurrentTile();

            // Bewegung veröffentlichen
            GameMessage moveMessage = new GameMessage(MessageType.PLAYER_MOVED, Map.of(
                    "playerId", player.getId(),
                    "pos", newIndex,
                    "dice", roll,
                    "tileName", landedTile.getLabel(),
                    "tileType", landedTile.getClass().getSimpleName()
            ));

            // Aktuellen Spielerwechsel vorbereiten
            gameState.advanceTurn();
            Player next = gameState.getCurrentPlayer();

            GameMessage nextPlayer = new GameMessage(MessageType.CURRENT_PLAYER, Map.of(
                    "playerId", next.getId()
            ));

            // Rückgabe: Kombinierte Antwort aus Bewegung und Zugwechsel
            return new GameMessage(MessageType.ROLL_DICE, Map.of(
                    "move", moveMessage.getPayload(),
                    "next", nextPlayer.getPayload()
            ));

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Würfeln: " + e.getMessage());
        }
    }
}