package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.dto.EmoteMessagePayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class EmoteRequest implements GameRequest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            EmoteMessagePayload emote = mapper.convertValue(payload, EmoteMessagePayload.class);

            System.out.println("🎭 Emote empfangen von " + emote.getSender() + ": " + emote.getEmote());

            return new GameMessage(lobbyId, MessageType.EMOTE, emote);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Fehler beim Parsen von Emote-Payload: " + e.getMessage());
            return new GameMessage(lobbyId, MessageType.ERROR, "Ungültiges Emote-Payload");
        }
    }
}
