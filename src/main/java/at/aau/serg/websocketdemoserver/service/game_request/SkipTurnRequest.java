package at.aau.serg.websocketdemoserver.service.game_request;


import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

import java.util.Map;

public class SkipTurnRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            int playerId = (int) payload.get("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            if (player.isSuspended()) {
                player.decreaseSuspension();

                return new GameMessage(
                        MessageType.SKIPPED,
                        Map.of(
                                "playerId", player.getId(),
                                "tilePos", player.getCurrentTile().getIndex(),
                                "tileName", player.getCurrentTile().getLabel()
                        )
                );
            } else {
                return new GameMessage(MessageType.ERROR, "Spieler ist nicht gesperrt.");
            }

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Rundenüberspringen: " + e.getMessage());
        }
    }
}

