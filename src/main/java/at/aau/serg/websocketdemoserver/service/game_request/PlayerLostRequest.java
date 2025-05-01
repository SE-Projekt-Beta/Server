package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

import java.util.*;

public class PlayerLostRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        try {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            int playerId = Integer.parseInt(payload.get("playerId").toString());

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                return new GameMessage(MessageType.ERROR, "Spieler nicht gefunden.");
            }

            // Alle Besitztümer löschen
            for (var street : player.getOwnedStreets()) {
                street.setOwner(null);
                street.clearBuildings();
            }

            // Spieler aus der Liste entfernen
            gameState.removePlayer(playerId);

            // Nachricht mit optionaler Info, ob das Spiel vorbei ist
            boolean gameOver = gameState.getAllPlayers().size() <= 1;
            return new GameMessage(
                    MessageType.PLAYER_LOST,
                    Map.of(
                            "playerId", playerId,
                            "gameOver", gameOver
                    )
            );

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Entfernen des Spielers: " + e.getMessage());
        }
    }
}
