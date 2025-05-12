package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RollDiceRequestTest {

    private GameState gameState;
    private Player player;
    private RollDiceRequest request;
    private List<GameMessage> extraMessages;
    private Dice mockDice;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        player = new Player("Tester", gameState.getBoard());
        gameState.startGame(List.of(player));
        extraMessages = new ArrayList<>();

        mockDice = new Dice(1, 1); // deterministisch: roll() == 1
        request = new RollDiceRequest(mockDice);
    }

    private Map<String, Object> buildPayload(int playerId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", playerId);
        return payload;
    }

    @Test
    void testValidRoll() {
        int lobbyId = 1;
        Map<String, Object> payload = buildPayload(player.getId());

        GameMessage result = request.execute(lobbyId, payload, gameState, extraMessages);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, extraMessages.size());

        GameMessage diceMessage = extraMessages.get(0);
        assertEquals(MessageType.DICE_ROLLED, diceMessage.getType());

        @SuppressWarnings("unchecked")
        Map<String, Object> dicePayload = (Map<String, Object>) diceMessage.getPayload();
        assertEquals(player.getId(), dicePayload.get("playerId"));
        assertEquals(1, dicePayload.get("steps"));
    }

    @Test
    void testWrongPlayerTurn() {
        Player p2 = new Player("WrongTurn", gameState.getBoard());
        gameState.startGame(List.of(player, p2));
        Map<String, Object> payload = buildPayload(p2.getId());

        GameMessage result = request.execute(123, payload, gameState, extraMessages);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Nicht dein Zug"));
    }

    @Test
    void testPlayerNotFound() {
        Map<String, Object> payload = buildPayload(999); // Nicht vorhanden
        GameMessage result = request.execute(123, payload, gameState, extraMessages);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }

    @Test
    void testExceptionHandling() {
        GameMessage result = request.execute(123, new Object(), gameState, extraMessages);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Würfeln"));
    }
}
