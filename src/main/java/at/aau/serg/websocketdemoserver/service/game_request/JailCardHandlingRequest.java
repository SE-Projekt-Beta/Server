package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class JailCardHandlingRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        int playerId = gameState.getCurrentPlayer().getId();
        Player player = gameState.getPlayer(playerId);

        if (!player.hasEscapeCard()) {
            return GameMessage.error("Du besitzt keine 'Aus dem Gefängnis frei'-Karte.");
        }

        player.setEscapeCard(false);
        player.resetSuspension();

        PlayerOutOfJailCardPayload payload = new PlayerOutOfJailCardPayload(
                player.getId(),
                player.getNickname()
        );

        return new GameMessage(MessageType.PLAYER_OUT_OF_JAIL_CARD, payload);
    }
}
