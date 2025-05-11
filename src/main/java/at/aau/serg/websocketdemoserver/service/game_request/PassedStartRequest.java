package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.CashTaskPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;

public class PassedStartRequest implements GameRequest {

    private static final int START_CASH = 200;
    private static final int EXACT_START_CASH = 400;
    private static final int START_TILE_INDEX = 1;

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder ausgeschieden.");
            }

            int bonus = (player.getCurrentTile().getIndex() == START_TILE_INDEX) ? EXACT_START_CASH : START_CASH;
            player.addCash(bonus);

            extraMessages.add(new GameMessage(
                    lobbyId,
                    MessageType.CASH_TASK,
                    new CashTaskPayload(player.getId(), bonus, player.getCash())
            ));

            gameState.advanceTurn();
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Startbonus: " + e.getMessage());
        }
    }
}

