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
import java.util.Map;

public class PassedStartRequest implements GameRequest {

    private static final int START_TILE_INDEX = 1;
    private static final int CASH_FOR_PASSING = 200;
    private static final int CASH_FOR_LANDING = 400;

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) payload;
            JSONObject obj = new JSONObject(map);

            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder ausgeschieden.");
            }

            boolean landedOnStart = player.getCurrentTile().getIndex() == START_TILE_INDEX;
            int bonus = landedOnStart ? CASH_FOR_LANDING : CASH_FOR_PASSING;

            player.addCash(bonus);

            if (landedOnStart) {
                // Nur wenn der Spieler direkt auf Start landet → Dialog + Turn-Ende
                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.PASS_START,
                        Map.of(
                                "playerId", playerId,
                                "amount", bonus,
                                "landed", true
                        )
                ));
                gameState.advanceTurn();
            } else {
                // Wenn er nur über Start kommt → stiller Bonus
                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.CASH_TASK,
                        new CashTaskPayload(playerId, bonus, player.getCash())
                ));
                // kein advanceTurn – restliche Feldlogik wird danach ausgeführt
            }

            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Startbonus: " + e.getMessage());
        }
    }
}
