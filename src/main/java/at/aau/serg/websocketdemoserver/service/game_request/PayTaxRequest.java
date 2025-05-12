package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class PayTaxRequest implements GameRequest {

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) payload;
            JSONObject obj = new JSONObject(map);

            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return MessageFactory.error(lobbyId, "Spieler nicht gefunden.");
            }

            if (playerId != gameState.getCurrentPlayerId()) {
                return MessageFactory.error(lobbyId, "Nicht dein Zug.");
            }

            Tile tile = player.getCurrentTile();
            if (!(tile instanceof SpecialTile specialTile) || specialTile.getType() != TileType.TAX) {
                return MessageFactory.error(lobbyId, "Kein Steuerfeld.");
            }

            int position = tile.getIndex();
            int taxAmount;

            // Steuerbetrag dynamisch aus Position bestimmen
            if (position == 21) {
                taxAmount = 200; // Sondersteuer
            } else if (position == 33) {
                taxAmount = 400; // Vermögensabgabe
            } else {
                return MessageFactory.error(lobbyId, "Unbekanntes Steuerfeld.");
            }

            boolean isBankrupt = player.adjustCash(-taxAmount);
            if (isBankrupt) {
                extraMessages.add(MessageFactory.playerLost(lobbyId, player.getId()));
            }

            gameState.advanceTurn();
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Steuerzahlen: " + e.getMessage());
        }
    }
}
