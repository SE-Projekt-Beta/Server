package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetLevel;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.DicePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RollDiceRequestTest {

    private GameState gameState;
    private Player player;
    private DicePair dicePair;
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

        // Mock-DicePair
        dicePair = mock(DicePair.class);
        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // deterministischer Wurf

        request = new RollDiceRequest(dicePair);
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

    @Test
    void testExecuteAlreadyRolledDice() {
        // Ensure it's Alice's turn
        gameState.setCurrentPlayerIndex(0);
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
        Player player2 = gameState.getAlivePlayers().get(1);
        player2.eliminate();

        // Ensure it's Alice's turn
        gameState.setCurrentPlayerIndex(0);
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_OVER, result.getType());
        assertTrue(extras.isEmpty());
    }

    @Test
    void testExecutePlayerSuspended() {
        // Ensure it's Alice's turn
        gameState.setCurrentPlayerIndex(0);
        player.suspendForRounds(3);

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, extras.size());
        assertEquals(MessageType.ASK_PAY_PRISON, extras.get(0).getType());
        Map<?, ?> extraPayload = (Map<?, ?>) extras.get(0).getPayload();
        assertEquals(player.getId(), extraPayload.get("playerId"));
        assertEquals(3, extraPayload.get("suspensionRounds"));
    }

    @Test
    void testPassingStart() {
        // Place player near the end of the board
        player.moveToTile(39); // Just before START
        gameState.setCurrentPlayerIndex(0);

        // Set dice to roll enough to pass START
        when(dicePair.roll()).thenReturn(new int[]{2, 1}); // Move 3 spaces, from 39 to 2

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(2, player.getCurrentTile().getIndex()); // Should end up on position 2
        assertEquals(2, extras.size()); // DICE_ROLLED message and a message from PassedStartRequest
    }

    @Test
    void testLandingDirectlyOnStart() {
        // Place player near the end of the board
        player.moveToTile(40); // One before START
        gameState.setCurrentPlayerIndex(0);

        // Set dice to roll exactly enough to land on START
        when(dicePair.roll()).thenReturn(new int[]{1, 0}); // Move 1 space, from 40 to 1 (START)

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(1, player.getCurrentTile().getIndex()); // Should end up on START (position 1)
        assertEquals(1, extras.size()); // Only DICE_ROLLED message, no other messages
        assertEquals(MessageType.DICE_ROLLED, extras.get(0).getType());
    }

    @Test
    void testLandingOnUnownedStreet() {
        // Setup: create a street tile and position player to land on it
        StreetTile streetTile = new StreetTile(3, "Test Street", 200, 50, StreetLevel.NORMAL, 100);
        gameState.getBoard().getTiles().set(2, streetTile); // Replace position 3

        player.moveToTile(1);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 spaces to position 3

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex());
        assertEquals(2, extras.size());

        // Check that player is asked to buy property
        boolean hasAskBuyProperty = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.ASK_BUY_PROPERTY);
        assertTrue(hasAskBuyProperty);
    }

    @Test
    void testLandingOnOwnedStreet() {
        // Setup: create a street tile owned by player 2
        Player player2 = gameState.getAlivePlayers().get(1);
        StreetTile streetTile = new StreetTile(3, "Test Street", 200, 50, StreetLevel.NORMAL, 100);
        streetTile.setOwner(player2);
        gameState.getBoard().getTiles().set(2, streetTile); // Replace position 3

        // Give the players some money to start
        player.setCash(1000);
        player2.setCash(1000);

        // Position player 1 to land on owned street
        player.moveToTile(1);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 spaces to position 3

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex());

        // Check that rent was paid
        assertEquals(950, player.getCash()); // 1000 - 50 (base rent)
        assertEquals(1050, player2.getCash()); // 1000 + 50

        // Turn should advance
        assertNotEquals(0, gameState.getCurrentPlayerIndex());
    }

    @Test
    void testLandingOnUnaffordableStreet() {
        // Setup: create an expensive street tile
        StreetTile streetTile = new StreetTile(3, "Expensive Street", 2000, 500, StreetLevel.PREMIUM, 1000);
        gameState.getBoard().getTiles().set(2, streetTile); // Replace position 3

        // Give player little money
        player.setCash(100);

        // Position player to land on the street
        player.moveToTile(1);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 spaces to position 3

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex());

        // Should have extra message about not being able to afford
        boolean hasExtraMessage = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.EXTRA_MESSAGE);
        assertTrue(hasExtraMessage);

        // Turn should advance
        assertNotEquals(0, gameState.getCurrentPlayerIndex());
    }

    @Test
    void testLandingOnGotoJail() {
        // Set up a GOTO_JAIL tile
        SpecialTile jailTile = new SpecialTile(31, "Jail", TileType.PRISON);
        gameState.getBoard().getTiles().set(30, jailTile);

        SpecialTile gotoJailTile = new SpecialTile(5, "Go To Jail", TileType.GOTO_JAIL);
        gameState.getBoard().getTiles().set(4, gotoJailTile);

        // Position player to land on GO TO JAIL
        player.moveToTile(2);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // Move 3 spaces to position 5

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(31, player.getCurrentTile().getIndex()); // Should be moved to jail
        assertTrue(player.isSuspended());
        assertEquals(3, player.getSuspensionRounds());

        // Should have GO_TO_JAIL message
        boolean hasGoToJail = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.GO_TO_JAIL);
        assertTrue(hasGoToJail);

        // Turn should advance
        assertNotEquals(0, gameState.getCurrentPlayerIndex());
    }

    @Test
    void testJailWithEscapeCard() {
        // Set up a GOTO_JAIL tile
        SpecialTile jailTile = new SpecialTile(31, "Jail", TileType.PRISON);
        gameState.getBoard().getTiles().set(30, jailTile);

        SpecialTile gotoJailTile = new SpecialTile(5, "Go To Jail", TileType.GOTO_JAIL);
        gameState.getBoard().getTiles().set(4, gotoJailTile);

        // Give player an escape card
        player.setEscapeCard(true);

        // Position player to land on GO TO JAIL
        player.moveToTile(2);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // Move 3 spaces to position 5

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertFalse(player.isSuspended()); // Should not be suspended
        assertFalse(player.hasEscapeCard()); // Card should be used

        // Should have DRAW_RISK_CARD message about using freedom card
        boolean hasDrawRiskCard = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.DRAW_RISK_CARD);
        assertTrue(hasDrawRiskCard);
    }

    @Test
    void testLandingOnBankTile() {
        // Set up a BANK tile
        SpecialTile bankTile = new SpecialTile(5, "Bank", TileType.BANK);
        gameState.getBoard().getTiles().set(4, bankTile);

        // Position player to land on BANK
        player.moveToTile(2);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // Move 3 spaces to position 5

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        request = spy(request);
        doReturn(new GameMessage(lobbyId, MessageType.GAME_STATE, Map.of("test", "bank")))
            .when(request).execute(eq(lobbyId), any(), eq(gameState), eq(extras));

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // Verify DrawBankCardRequest was executed
        verify(request).execute(eq(lobbyId), any(), eq(gameState), eq(extras));
    }

    @Test
    void testLandingOnRiskTile() {
        // Set up a RISK tile
        SpecialTile riskTile = new SpecialTile(5, "Risk", TileType.RISK);
        gameState.getBoard().getTiles().set(4, riskTile);

        // Position player to land on RISK
        player.moveToTile(2);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // Move 3 spaces to position 5

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        request = spy(request);
        doReturn(new GameMessage(lobbyId, MessageType.GAME_STATE, Map.of("test", "risk")))
            .when(request).execute(eq(lobbyId), any(), eq(gameState), eq(extras));

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // Verify DrawRiskCardRequest was executed
        verify(request).execute(eq(lobbyId), any(), eq(gameState), eq(extras));
    }

    @Test
    void testLandingOnTaxTile() {
        // Set up a TAX tile
        SpecialTile taxTile = new SpecialTile(5, "Tax", TileType.TAX);
        gameState.getBoard().getTiles().set(4, taxTile);

        // Position player to land on TAX
        player.moveToTile(2);
        gameState.setCurrentPlayerIndex(0);

        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // Move 3 spaces to position 5

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        request = spy(request);
        doReturn(new GameMessage(lobbyId, MessageType.GAME_STATE, Map.of("test", "tax")))
            .when(request).execute(eq(lobbyId), any(), eq(gameState), eq(extras));

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // Verify PayTaxRequest was executed
        verify(request).execute(eq(lobbyId), any(), eq(gameState), eq(extras));
    }

    @Test
    void testExceptionHandling() {
        // Mock DicePair to throw exception
        when(dicePair.roll()).thenThrow(new RuntimeException("Test exception"));

        // Setup for the test
        gameState.setCurrentPlayerIndex(0);
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Würfeln"));
    }
}
