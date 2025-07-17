package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.service.EmoteRateLimiter;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.dto.EmoteMessagePayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class EmoteRequest implements GameRequest {
    private final ObjectMapper mapper = new ObjectMapper();

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
            return MessageFactory.emoteError(lobbyId, "Emote-Limit erreicht - bitte warte kurz");
        }

        // Emote an andere Spieler senden
        return MessageFactory.emote(lobbyId, sender, emote);
    }

    /*@Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            EmoteMessagePayload emote = mapper.convertValue(payload, EmoteMessagePayload.class);

            System.out.println("🎭 Emote empfangen von " + emote.getSender() + ": " + emote.getEmote());

            return new GameMessage(lobbyId, MessageType.EMOTE, emote);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Fehler beim Parsen von Emote-Payload: " + e.getMessage());
            return new GameMessage(lobbyId, MessageType.ERROR, "Ungültiges Emote-Payload");
        }
    }*/
}
