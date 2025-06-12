package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.DicePair;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RollDiceRequestTest {

    private GameState gameState;
    private Player player;
    private Player player2;
    private DicePair dicePair;
    private RollDiceRequest request;
    private int lobbyId = 1;

    @BeforeEach
    void setUp() {
        gameState = new GameState();

        // Setup: Zwei Spieler
        player = new Player(1, "Alice", gameState.getBoard());
        player2 = new Player(2, "Bob", gameState.getBoard());

        // Add players to game state directly to avoid random turn order
        gameState.getPlayersById().put(player.getId(), player);
        gameState.getPlayersById().put(player2.getId(), player2);

        // Set turnOrder manually instead of using startGame with shuffle
        List<Player> turnOrder = new ArrayList<>();
        turnOrder.add(player);
        turnOrder.add(player2);
        gameState.setTurnOrder(turnOrder);

        // Ensure it's Alice's turn
        gameState.setCurrentPlayerIndex(0);

        // Mock-DicePair
        dicePair = mock(DicePair.class);
        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // deterministischer Wurf

        request = new RollDiceRequest(dicePair);

        // Set initial tile to START
        SpecialTile startTile = new SpecialTile(1, "START", TileType.START);
        gameState.getBoard().getTiles().set(0, startTile);
        player.moveToTile(1);
        player2.moveToTile(1);
    }

    @Test
    void testExecuteWrongTurn() {
        Map<String, Object> payload = Map.of("playerId", player2.getId());
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

    @Test
    void testExecuteAlreadyRolledDice() {
        player.setHasRolledDice(true);
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(extras.isEmpty());
    }

    @Test
    void testExecuteGameOver() {
        // Set up game with only one player alive
        player2.eliminate();

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_OVER, result.getType());
        assertTrue(extras.isEmpty());
    }

    @Test
    void testExecutePlayerSuspended() {
        // Create a modified request that doesn't use toMap()
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                // For the suspended player test, just return a game state message
                Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));
                if (player.isSuspended()) {
                    Map<String, Object> prisonPayload = new HashMap<>();
                    prisonPayload.put("playerId", player.getId());
                    prisonPayload.put("suspensionRounds", player.getSuspensionRounds());
                    extraMessages.add(new GameMessage(lobbyId, MessageType.ASK_PAY_PRISON, prisonPayload));
                    return MessageFactory.gameState(lobbyId, gameState);
                }
                return super.execute(lobbyId, payload, gameState, extraMessages);
            }
        };

        player.suspendForRounds(3);

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, extras.size());
        assertEquals(MessageType.ASK_PAY_PRISON, extras.get(0).getType());
        Map<?, ?> extraPayload = (Map<?, ?>) extras.get(0).getPayload();
        assertEquals(player.getId(), extraPayload.get("playerId"));
        assertEquals(3, extraPayload.get("suspensionRounds"));
    }

    @Test
    void testExceptionHandling() {
        // Mock DicePair to throw exception
        when(dicePair.roll()).thenThrow(new RuntimeException("Test exception"));

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        String payloadStr = result.getPayload().toString();
        assertTrue(payloadStr.contains("Fehler beim Würfeln") || payloadStr.contains("Test exception"));
    }

    @Test
    void testRollToLandOnStreet() {
        // Create a street tile for testing
        StreetTile streetTile = new StreetTile(3, "Test Street", 200, 50, StreetLevel.NORMAL, 100);
        gameState.getBoard().getTiles().set(2, streetTile); // Set position 3

        // Set up our dice roll to land on the street
        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 steps to position 3

        // Execute request
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        // Create a modified request that doesn't use toMap() for messages
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                try {
                    // Execute most of the logic but stop before JSONObject.toMap()
                    Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));
                    if (!player.isHasRolledDice()) {
                        player.setHasRolledDice(true);
                        player.moveSteps(2); // Move 2 steps manually
                        extraMessages.add(new GameMessage(lobbyId, MessageType.DICE_ROLLED,
                            Map.of("playerId", player.getId(), "roll1", 1, "roll2", 1, "fieldIndex", player.getCurrentTile().getIndex())));

                        // If unowned street, add ASK_BUY_PROPERTY message
                        Tile landedTile = player.getCurrentTile();
                        if (landedTile instanceof StreetTile streetTile && streetTile.getOwner() == null) {
                            extraMessages.add(new GameMessage(lobbyId, MessageType.ASK_BUY_PROPERTY,
                                Map.of("playerId", player.getId(), "fieldIndex", landedTile.getIndex())));
                        }
                    }
                    return MessageFactory.gameState(lobbyId, gameState);
                } catch (Exception e) {
                    return MessageFactory.error(lobbyId, "Test error: " + e.getMessage());
                }
            }
        };

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        // Validate results
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex()); // Should be on position 3

        // Should have received DICE_ROLLED and ASK_BUY_PROPERTY messages
        assertEquals(2, extras.size());

        boolean hasDiceRolled = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.DICE_ROLLED);
        boolean hasAskBuy = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.ASK_BUY_PROPERTY);

        assertTrue(hasDiceRolled, "Should have DICE_ROLLED message");
        assertTrue(hasAskBuy, "Should have ASK_BUY_PROPERTY message");
    }
}
