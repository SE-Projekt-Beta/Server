package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;

public class RollDiceRequest implements GameRequest {

    private final Dice dice;

    public RollDiceRequest(Dice dice) {
        this.dice = dice;
    }

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return MessageFactory.error(lobbyId, "Spieler nicht gefunden.");
            }

            if (playerId != gameState.getCurrentPlayerId()) {
                return MessageFactory.error(lobbyId, "Nicht dein Zug.");
            }

            int steps = dice.roll();
            player.moveSteps(steps);

            extraMessages.add(new GameMessage(
                    lobbyId,
                    MessageType.DICE_ROLLED,
                    new JSONObject().put("playerId", playerId).put("steps", steps).toMap()
            ));

            // Kein advanceTurn() hier – Zug ist **noch nicht beendet**.
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Würfeln: " + e.getMessage());
        }
    }
}
