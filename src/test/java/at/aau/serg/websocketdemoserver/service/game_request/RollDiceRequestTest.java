package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RollDiceRequestTest {

    private GameState gameState;
    private Player player;
    private Dice dice;
    private RollDiceRequest request;
    private int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();

        // Setup: Zwei Spieler
        player = new Player(1, "Alice", gameState.getBoard());
        Player player2 = new Player(2, "Bob", gameState.getBoard());

        List<Player> players = new ArrayList<>(List.of(player, player2));
        gameState.startGame(players);

        // Mock-Dice
        dice = mock(Dice.class);
        when(dice.roll()).thenReturn(3); // deterministischer Wurf

        request = new RollDiceRequest(dice);
    }

    @Test
    void testExecuteWrongTurn() {
        Player notMyTurn = gameState.getAlivePlayers().get(1);
        Map<String, Object> payload = Map.of("playerId", notMyTurn.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Nicht dein Zug"));
        assertTrue(extras.isEmpty());
    }

    @Test
    void testExecuteInvalidPlayer() {
        Map<String, Object> payload = Map.of("playerId", 999); // ungültiger Player
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
        assertTrue(extras.isEmpty());
    }
}
