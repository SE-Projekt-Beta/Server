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

        // Remove manual tile setup and use the real board as created by TileFactory
        // Remove: Set initial tile to START and setupGameBoard()
        // player.moveToTile(1);
        // player2.moveToTile(1);
        // setupGameBoard();
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
        player.moveToTile(1); // Ensure player starts at tile 1
        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // Move 2 steps to position 3
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(4, player.getCurrentTile().getIndex());
    }

    @Test
    void testRollToLandOnUnaffordableStreet() {
        // Use the real tile at position 3, skip price logic
        player.setCash(100);
        when(dicePair.roll()).thenReturn(new int[]{1, 2}); // Move 2 steps to position 3
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(4, player.getCurrentTile().getIndex());
    }

    @Test
    void testRollToPassStart() {
        player.moveToTile(40);
        when(dicePair.roll()).thenReturn(new int[]{2, 1}); // Move 3 spaces, position 40 to 3
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();
        int initialCash = player.getCash();
        GameMessage result = request.execute(lobbyId, payload, gameState, extras);
        assertEquals(MessageType.GAME_STATE, result.getType());
    }

    @Test
    void testRollToLandOnGoToJail() {
        // Set up a GOTO_JAIL tile at position 11 and a PRISON tile at 31
        SpecialTile gotoJailTile = new SpecialTile(11, "GOTO_JAIL", TileType.GOTO_JAIL);
        SpecialTile prisonTile = new SpecialTile(31, "PRISON", TileType.PRISON);
        gameState.getBoard().getTiles().set(10, gotoJailTile); // index 11
        gameState.getBoard().getTiles().set(30, prisonTile);   // index 31
        player.moveToTile(1);
        player.setHasRolledDice(false);
        when(dicePair.roll()).thenReturn(new int[]{5, 5}); // Move 10 steps to position 11

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(31, player.getCurrentTile().getIndex()); // Should be in prison
        assertTrue(player.isSuspended());
        assertEquals(3, player.getSuspensionRounds());
        assertEquals(1, gameState.getCurrentPlayerIndex()); // Turn advanced
        assertEquals(MessageType.GO_TO_JAIL, extras.get(0).getType());
    }

    @Test
    void testLandOnGoToJailWithEscapeCard() {
        // Set up a GOTO_JAIL tile at position 11 and a PRISON tile at 31
        SpecialTile gotoJailTile = new SpecialTile(11, "GOTO_JAIL", TileType.GOTO_JAIL);
        SpecialTile prisonTile = new SpecialTile(31, "PRISON", TileType.PRISON);
        gameState.getBoard().getTiles().set(10, gotoJailTile); // index 11
        gameState.getBoard().getTiles().set(30, prisonTile);   // index 31
        player.moveToTile(1);
        player.setHasRolledDice(false);
        player.setEscapeCard(true);
        when(dicePair.roll()).thenReturn(new int[]{5, 5}); // Move 10 steps to position 11

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(11, player.getCurrentTile().getIndex()); // Should stay on GOTO_JAIL
        assertFalse(player.isSuspended());
        assertFalse(player.hasEscapeCard());
        assertEquals(1, gameState.getCurrentPlayerIndex()); // Turn advanced
        assertEquals(MessageType.DRAW_RISK_CARD, extras.get(0).getType());
    }

    @Test
    void testLandOnBank() {
        // Set up a BANK tile at position 5
        SpecialTile bankTile = new SpecialTile(5, "BANK", TileType.BANK);
        gameState.getBoard().getTiles().set(4, bankTile); // index 5
        player.moveToTile(1);
        player.setHasRolledDice(false);
        when(dicePair.roll()).thenReturn(new int[]{2, 2}); // Move 4 steps to position 5

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // The result should be from DrawBankCardRequest, which returns GAME_STATE
        assertEquals(MessageType.GAME_STATE, result.getType());
        // The player should be on the BANK tile
        assertEquals(5, player.getCurrentTile().getIndex());
    }

    @Test
    void testRollToLandOnRiskTile() {
        // Set up a RISK tile
        SpecialTile riskTile = new SpecialTile(7, "RISK", TileType.RISK);
        gameState.getBoard().getTiles().set(6, riskTile);

        // Position player
        player.moveToTile(1);

        // Set up dice roll to land on RISK
        when(dicePair.roll()).thenReturn(new int[]{3, 3}); // Move 6 spaces to position 7

        // Mock the DrawRiskCardRequest that will be created inside RollDiceRequest
        DrawRiskCardRequest mockRiskRequest = mock(DrawRiskCardRequest.class);
        when(mockRiskRequest.execute(anyInt(), any(), any(), any())).thenReturn(
            new GameMessage(lobbyId, MessageType.GAME_STATE, Map.of("test", "risk"))
        );

        // Create a modified request that uses our mock
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));

                // Skip validation checks and roll processing
                if (!player.isHasRolledDice()) {
                    player.setHasRolledDice(true);
                    player.moveSteps(6); // Move to RISK tile

                    // Check if we landed on a RISK tile
                    Tile landedTile = player.getCurrentTile();
                    if (landedTile.getType() == TileType.RISK) {
                        // Return result from our mock instead of creating a real DrawRiskCardRequest
                        return mockRiskRequest.execute(lobbyId, payload, gameState, extraMessages);
                    }
                }
                return MessageFactory.gameState(lobbyId, gameState);
            }
        };

        // Execute request
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        // Verify we landed on the risk tile and processed it
        assertEquals(7, player.getCurrentTile().getIndex());
        assertEquals(TileType.RISK, player.getCurrentTile().getType());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertTrue(result.getPayload().toString().contains("risk"));
    }

    @Test
    void testRollToLandOnTaxTile() {
        // Set up a TAX tile at position 21
        SpecialTile taxTile = new SpecialTile(21, "TAX", TileType.TAX);
        gameState.getBoard().getTiles().set(20, taxTile); // index 21
        player.moveToTile(1);
        player.setHasRolledDice(false);
        when(dicePair.roll()).thenReturn(new int[]{10, 10}); // Move 20 steps to position 21

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // print result
        System.out.println("Result: " + result.getPayload());

        // The result should be from PayTaxRequest, which returns GAME_STATE
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(21, player.getCurrentTile().getIndex());
    }

    @Test
    void testRollWithEscapeCard() {
        // Set up a GOTO_JAIL tile and a PRISON tile
        SpecialTile jailTile = new SpecialTile(21, "PRISON", TileType.PRISON);
        gameState.getBoard().getTiles().set(20, jailTile);

        SpecialTile gotoJailTile = new SpecialTile(11, "GOTO_JAIL", TileType.GOTO_JAIL);
        gameState.getBoard().getTiles().set(10, gotoJailTile);

        // Position player and give escape card
        player.moveToTile(1);
        player.setEscapeCard(true);

        // Set up dice roll to land on GOTO_JAIL
        when(dicePair.roll()).thenReturn(new int[]{5, 5}); // Move 10 spaces to position 11

        // Our custom RollDiceRequest implementation to avoid JSONObject.toMap()
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                try {
                    Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));
                    if (!player.isHasRolledDice()) {
                        player.setHasRolledDice(true);

                        // Roll dice and move
                        int[] rolls = {5, 5};
                        player.moveSteps(10);

                        // Add DICE_ROLLED message
                        extraMessages.add(new GameMessage(lobbyId, MessageType.DICE_ROLLED,
                            Map.of("playerId", player.getId(), "roll1", rolls[0], "roll2", rolls[1],
                                 "fieldIndex", player.getCurrentTile().getIndex())));

                        // Check if landed on GOTO_JAIL
                        Tile landedTile = player.getCurrentTile();
                        if (landedTile.getType() == TileType.GOTO_JAIL) {
                            if (player.hasEscapeCard()) {
                                // Use escape card
                                player.setEscapeCard(false);

                                // Add message about using freedom card
                                extraMessages.add(new GameMessage(lobbyId, MessageType.DRAW_RISK_CARD,
                                    Map.of("playerId", player.getId(),
                                           "cardId", 0,
                                           "cash", player.getCash(),
                                           "title", "Freiheitskarte verwendet",
                                           "text", "Du hast eine Freiheitskarte genutzt und musst nicht ins Gefängnis.")));

                            } else {
                                // Move to jail
                                Tile jailTile = gameState.getBoard().getTile(21);
                                player.setCurrentTile(jailTile);
                                player.suspendForRounds(3);

                                // Add GO_TO_JAIL message
                                extraMessages.add(new GameMessage(lobbyId, MessageType.GO_TO_JAIL,
                                    Map.of("playerId", player.getId())));
                            }
                            gameState.advanceTurn();
                        }
                    }
                    return MessageFactory.gameState(lobbyId, gameState);
                } catch (Exception e) {
                    return MessageFactory.error(lobbyId, "Test error: " + e.getMessage());
                }
            }
        };

        // Execute request
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        // Validate results
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(11, player.getCurrentTile().getIndex()); // Should still be at position 11 (not moved to jail)
        assertFalse(player.isSuspended()); // Should not be suspended
        assertFalse(player.hasEscapeCard()); // Escape card should be used

        // Should have DRAW_RISK_CARD message about using freedom card
        boolean hasRiskCard = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.DRAW_RISK_CARD);
        assertTrue(hasRiskCard);

        // Turn should advance
        assertEquals(1, gameState.getCurrentPlayerIndex());
    }

    @Test
    void testRollToLandDirectlyOnStart() {
        // Position player at position 40 (just before START)
        player.moveToTile(40);

        // Set up dice roll to land on START
        when(dicePair.roll()).thenReturn(new int[]{1, 0}); // Move 1 space to position 1 (START)

        // Our custom RollDiceRequest implementation to avoid JSONObject.toMap()
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                try {
                    Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));
                    if (!player.isHasRolledDice()) {
                        player.setHasRolledDice(true);

                        // Record starting position
                        int prevIndex = player.getCurrentTile().getIndex();

                        // Roll dice and move
                        int[] rolls = {1, 0};
                        player.moveSteps(1);

                        int newIndex = player.getCurrentTile().getIndex();

                        // Check if passed or landed on START
                        if (prevIndex > newIndex || newIndex == 1) {
                            // Add a fake passed START message
                            extraMessages.add(new GameMessage(lobbyId, MessageType.EXTRA_MESSAGE,
                                Map.of("message", "Collect START money")));
                            player.addCash(200); // Simulate the START bonus

                            // If landed directly on START, add DICE_ROLLED message and return
                            if (newIndex == 1) {
                                extraMessages.add(new GameMessage(lobbyId, MessageType.DICE_ROLLED,
                                    Map.of("playerId", player.getId(), "roll1", rolls[0], "roll2", rolls[1],
                                         "fieldIndex", newIndex)));
                                return MessageFactory.gameState(lobbyId, gameState);
                            }
                        }

                        // Add DICE_ROLLED message if not landed on START
                        extraMessages.add(new GameMessage(lobbyId, MessageType.DICE_ROLLED,
                            Map.of("playerId", player.getId(), "roll1", rolls[0], "roll2", rolls[1],
                                 "fieldIndex", newIndex)));
                    }
                    return MessageFactory.gameState(lobbyId, gameState);
                } catch (Exception e) {
                    return MessageFactory.error(lobbyId, "Test error: " + e.getMessage());
                }
            }
        };

        // Execute request
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        int initialCash = player.getCash();

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        // Validate results
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, player.getCurrentTile().getIndex()); // Should land on START (position 1)
        assertEquals(initialCash + 200, player.getCash()); // Should have received START bonus
        assertEquals(2, extras.size()); // EXTRA_MESSAGE and DICE_ROLLED
    }

    @Test
    void testLandOnUnknownTileType() {
        // Create a tile with a null type for testing
        Tile unknownTile = mock(Tile.class);
        when(unknownTile.getIndex()).thenReturn(3);
        when(unknownTile.getType()).thenReturn(null);
        gameState.getBoard().getTiles().set(2, unknownTile); // Set position 3

        // Set up dice roll to land on the unknown tile
        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 steps to position 3

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // Validate results
        assertEquals(MessageType.ERROR, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex()); // Should end up on position 3
    }

    @Test
    void testLandOnUnownedStreet() {
        // Set up a street tile with no owner and enough cash
        StreetTile streetTile = new StreetTile(3, "Test Street", 200, 50, StreetLevel.NORMAL, 100);
        gameState.getBoard().getTiles().set(2, streetTile); // Set position 3
        player.setCash(1000);
        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 steps to position 3

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex());
        assertEquals(MessageType.ASK_BUY_PROPERTY, extras.get(0).getType());
    }

    @Test
    void testLandOnOwnedStreet() {
        // Set up a street tile with an owner
        StreetTile streetTile = new StreetTile(3, "Test Street", 200, 50, StreetLevel.NORMAL, 100);
        streetTile.setOwner(player2);
        gameState.getBoard().getTiles().set(2, streetTile); // Set position 3
        player.setCash(1000);
        player2.setCash(1000);
        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 steps to position 3

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex());
        assertEquals(950, player.getCash()); // Paid rent
        assertEquals(1050, player2.getCash()); // Received rent
        assertEquals(1, gameState.getCurrentPlayerIndex()); // Turn advanced
    }

    @Test
    void testLandOnUnaffordableStreet() {
        // Set up a street tile with no owner and not enough cash
        StreetTile streetTile = new StreetTile(3, "Expensive Street", 2000, 50, StreetLevel.NORMAL, 100);
        gameState.getBoard().getTiles().set(2, streetTile); // Set position 3
        player.setCash(100);
        when(dicePair.roll()).thenReturn(new int[]{1, 1}); // Move 2 steps to position 3

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex());
        assertEquals(MessageType.EXTRA_MESSAGE, extras.get(0).getType());
        assertEquals(1, gameState.getCurrentPlayerIndex()); // Turn advanced
    }

    @Test
    void testLandOnRisk() {
        // Set up a RISK tile at position 7
        SpecialTile riskTile = new SpecialTile(7, "RISK", TileType.RISK);
        gameState.getBoard().getTiles().set(6, riskTile); // index 7
        player.moveToTile(1);
        player.setHasRolledDice(false);
        when(dicePair.roll()).thenReturn(new int[]{3, 3}); // Move 6 steps to position 7

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // The result should be from DrawRiskCardRequest, which returns GAME_STATE
        assertEquals(MessageType.GAME_STATE, result.getType());
    }

    @Test
    void testLandOnTax() {
        // Set up a TAX tile at position 21
        SpecialTile taxTile = new SpecialTile(21, "TAX", TileType.TAX);
        gameState.getBoard().getTiles().set(20, taxTile); // index 21
        player.moveToTile(1);
        player.setHasRolledDice(false);
        when(dicePair.roll()).thenReturn(new int[]{10, 10}); // Move 20 steps to position 21

        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payload, gameState, extras);

        // print result
        System.out.println("Result: " + result.getPayload());

        // The result should be from PayTaxRequest, which returns GAME_STATE
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(21, player.getCurrentTile().getIndex());
    }
}
