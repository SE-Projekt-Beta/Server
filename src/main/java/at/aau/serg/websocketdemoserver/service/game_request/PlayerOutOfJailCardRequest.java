package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class PlayerOutOfJailCardRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        Player player = gameState.getCurrentPlayer();

        if (player == null) {
            return GameMessage.error("Kein Spieler vorhanden.");
        }

        if (!player.hasEscapeCard()) {
            return GameMessage.error("Du besitzt keine Gefängnis-Freikarte.");
        }

        player.setEscapeCard(false);

        PlayerOutOfJailCardPayload payload = new PlayerOutOfJailCardPayload(
                player.getId(),
                "Du bist aus dem Gefängnis entkommen."
        );

        return new GameMessage(MessageType.TURN_CONTINUES, payload);
    }
}
