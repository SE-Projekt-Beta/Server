package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.EmoteRateLimiter;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.MessageFactory;

import java.util.List;
import java.util.Map;

public class EmoteRequest implements GameRequest {
    private final EmoteRateLimiter rateLimiter = new EmoteRateLimiter();

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        if (!(payload instanceof Map payloadMap)) {
            return MessageFactory.error(lobbyId, "Ungültiges Emote-Payload.");
        }

        String sender = (String) payloadMap.get("sender");
        String emote = (String) payloadMap.get("emote");

        if (sender == null || emote == null) {
            return MessageFactory.error(lobbyId, "Fehlende Emote-Daten.");
        }

        if (!rateLimiter.canSend(sender)) {
            Player senderPlayer = gameState.findPlayerByName(sender);
            if (senderPlayer == null) {
                return MessageFactory.error(lobbyId, "Unbekannter Spieler: " + sender);
            }
            int senderId = senderPlayer.getId();

            // Fehlernachricht nur an den einen Spieler senden, also über extraMessages
            extraMessages.add(MessageFactory.emoteError(lobbyId, senderId, "Emote-Limit erreicht – bitte warte kurz"));
            return null; // keine Broadcast-Message an alle
        }

        // Emote an alle Spieler senden
        return MessageFactory.emote(lobbyId, sender, emote);
    }
}
