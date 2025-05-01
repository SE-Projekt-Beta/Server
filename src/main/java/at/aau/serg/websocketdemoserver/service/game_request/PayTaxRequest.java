package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class PayTaxRequest implements GameHandlerInterface {

    private static final double TAX_PERCENTAGE = 0.25;
    private static final int MAX_TAX_AMOUNT = 400;
    private static final int STATIC_TAX_AMOUNT = 300;

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            JSONObject obj = new JSONObject(message.getPayload().toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            Tile tile = player.getCurrentTile();
            if (!(tile instanceof SpecialTile)) {
                return new GameMessage(MessageType.ERROR, "Kein Steuerfeld.");
            }

            String tileName = tile.getLabel();
            int oldCash = player.getCash();
            int taxAmount;
            int newCash;

            if ("Vermögensabgabe".equals(tileName)) {
                taxAmount = (int) (oldCash * TAX_PERCENTAGE);
                if (MAX_TAX_AMOUNT > 0 && taxAmount > MAX_TAX_AMOUNT) {
                    taxAmount = MAX_TAX_AMOUNT;
                }
            } else if ("Sondersteuer".equals(tileName)) {
                taxAmount = STATIC_TAX_AMOUNT;
            } else {
                return new GameMessage(MessageType.ERROR, "Unbekanntes Steuerfeld.");
            }

            newCash = oldCash - taxAmount;
            if (newCash < 0) {
                return new GameMessage(MessageType.ERROR, "Spieler kann Steuer nicht zahlen.");
            }

            player.setCash(newCash);

            return new GameMessage(MessageType.PAY_TAX, Map.of(
                    "playerId", playerId,
                    "tileName", tileName,
                    "amount", taxAmount,
                    "oldCash", oldCash,
                    "newCash", newCash
            ));

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Steuerzahlen: " + e.getMessage());
        }
    }
}
