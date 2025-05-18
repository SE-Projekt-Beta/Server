package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.RiskCardDrawnPayload;
import at.aau.serg.websocketdemoserver.dto.WentToJailPayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RollPrisonRequest implements GameRequest {

    private final Dice dice;

    public RollPrisonRequest(Dice dice) {
        this.dice = dice;
    }

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) payload;
            JSONObject obj = new JSONObject(map);

            int playerId = obj.getInt("playerId");
            Player player = gameState.getPlayer(playerId);

            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder bereits ausgeschieden.");
            }

            int roll1 = dice.roll();
            int roll2 = dice.roll();

            System.out.println("Prison roll: " + player.getNickname() + " rolled " + roll1 + " and " + roll2);

            if (roll1 == roll2) {
                player.setSuspensionRounds(0);
                player.setHasEscapeCard(false);
                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.ROLLED_PRISON,
                        new JSONObject().put("playerId", playerId).put("roll1", roll1).put("roll2", roll2).toMap()
                ));
            } else {
                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.ROLLED_PRISON,
                        new JSONObject().put("playerId", playerId).put("roll1", roll1).put("roll2", roll2).toMap()
                ));
            }


            gameState.advanceTurn();
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Würfeln im Gefängnis: " + e.getMessage());
        }

    }

}