package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmoteRequestTest {
    private EmoteRequest emoteRequest;
    private GameState gameState;
    private List<GameMessage> extraMessages;

    @BeforeEach
    void setUp() {
        emoteRequest = new EmoteRequest();
        gameState = new GameState();
        extraMessages = new ArrayList<>();

        // Dummy-Spieler zur Verfügung stellen
        Player player = new Player(1, "Alice", new GameBoard());
        gameState.startGame(List.of(player));
    }

    @Test
    void execute_withInvalidPayload_returnsError() {
        Object invalidPayload = "not a map";

        GameMessage result = emoteRequest.execute(1, invalidPayload, gameState, extraMessages);

        assertNotNull(result);
        assertTrue(result.getPayload().toString().contains("Ungültiges Emote-Payload"));
    }

    @Test
    void execute_withMissingSenderOrEmote_returnsError() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", "Alice");
        // kein Emote gesetzt

        GameMessage result = emoteRequest.execute(1, payload, gameState, extraMessages);

        assertNotNull(result);
        assertTrue(result.getPayload().toString().contains("Fehlende Emote-Daten"));
    }

    @Test
    void execute_withValidPayloadAndAllowedEmote_returnsBroadcastMessage() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", "Alice");
        payload.put("emote", "smile");

        GameMessage result = emoteRequest.execute(1, payload, gameState, extraMessages);

        assertNotNull(result);
        assertEquals("EMOTE", result.getType().name());
        assertTrue(result.getPayload().toString().contains("smile"));
        assertTrue(extraMessages.isEmpty());
    }

    @Test
    void execute_withTooManyEmotes_returnsExtraMessageOnly() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", "Alice");
        payload.put("emote", "smile");

        // Emotes senden, bis Rate-Limit erreicht ist (5 laut EmoteRateLimiter)
        for (int i = 0; i < 5; i++) {
            emoteRequest.execute(1, payload, gameState, new ArrayList<>());
        }

        // 6. Versuch → sollte limitiert werden
        GameMessage result = emoteRequest.execute(1, payload, gameState, extraMessages);

        assertNull(result); // keine Broadcast-Message
        assertEquals(1, extraMessages.size());
        GameMessage errorMessage = extraMessages.get(0);
        assertEquals("EMOTE_ERROR", errorMessage.getType().name());
        assertTrue(errorMessage.getPayload().toString().contains("Emote-Limit"));
    }

    @Test
    void execute_rateLimitedButUnknownPlayer_returnsError() {
        // Name nicht in GameState enthalten
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", "UnknownUser");
        payload.put("emote", "hi");

        // Spamme limit bewusst (RateLimiter ist neu → immer true, also tricksen)
        for (int i = 0; i < 10; i++) {
            emoteRequest.execute(1, payload, gameState, new ArrayList<>());
        }

        GameMessage result = emoteRequest.execute(1, payload, gameState, extraMessages);

        assertNotNull(result);
        assertTrue(result.getPayload().toString().contains("Unbekannter Spieler"));
    }
}
