package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.cards.ActionCardFactory;
import at.aau.serg.websocketdemoserver.model.board.BankTile;
import at.aau.serg.websocketdemoserver.model.board.RiskTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;
import org.json.JSONObject;

import java.util.Map;

public class DrawEventCardRequest implements GameHandlerInterface {

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
            if (tile instanceof RiskTile || tile instanceof BankTile) {
                ActionCard card = ActionCardFactory.drawCard(tile);
                card.execute(player);

                MessageType type = tile instanceof RiskTile
                        ? MessageType.DRAW_EVENT_RISIKO_CARD
                        : MessageType.DRAW_EVENT_BANK_CARD;

                return new GameMessage(type, Map.of(
                        "title", card.getTitle(),
                        "description", card.getDescription()
                ));
            } else {
                return new GameMessage(MessageType.SKIPPED, Map.of(
                        "playerId", playerId,
                        "tilePos", tile.getIndex(),
                        "tileName", tile.getLabel()
                ));
            }

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Ziehen einer Ereigniskarte: " + e.getMessage());
        }
    }
}