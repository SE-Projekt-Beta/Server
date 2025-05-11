package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.EventCardDrawnPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;

public class GoToJailRequest implements GameRequest {

    private final Tile jailTile;

    public GoToJailRequest(Tile jailTile) {
        this.jailTile = jailTile;
    }

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder ausgeschieden.");
            }

            // Verarbeite den Gefängnisaufenthalt
            handleGoToJail(player, lobbyId, extraMessages);

            gameState.advanceTurn();
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler bei Gefängnisanweisung: " + e.getMessage());
        }
    }

    private void handleGoToJail(Player player, int lobbyId, List<GameMessage> extraMessages) {
        if (player.hasEscapeCard()) {
            player.setEscapeCard(false);
            extraMessages.add(new GameMessage(
                    lobbyId,
                    MessageType.DRAW_RISK_CARD,
                    new EventCardDrawnPayload(
                            "Freiheitskarte verwendet",
                            "Du hast eine Freiheitskarte genutzt und musst nicht ins Gefängnis.")
            ));
        } else {
            player.setCurrentTile(jailTile);
            player.suspendForRounds(3);
        }
    }
}
