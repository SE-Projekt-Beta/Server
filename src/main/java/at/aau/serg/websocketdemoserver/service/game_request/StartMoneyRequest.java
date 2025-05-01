package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class StartMoneyRequest implements GameHandlerInterface {

    private static final int BONUS_AMOUNT = 200;
    private static final String START_TILE_NAME = "Start";

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            int oldCash = player.getCash();
            player.setCash(oldCash + BONUS_AMOUNT);

            return new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, Map.of(
                    "title", "Startfeld",
                    "description", "Du hast das Startfeld überquert und erhältst " + BONUS_AMOUNT + " DKT-Dollar."
            ));
        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler bei Startgeldvergabe: " + e.getMessage());
        }
    }
}
