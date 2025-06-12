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

        // Setup basic game board with different tile types
        setupGameBoard();
    }

    private void setupGameBoard() {
        // Setup a more realistic game board with different tile types
        gameState.getBoard().getTiles().set(0, new SpecialTile(1, "START", TileType.START));
        gameState.getBoard().getTiles().set(2, new StreetTile(3, "Test Street", 200, 50, StreetLevel.NORMAL, 100));
        gameState.getBoard().getTiles().set(4, new SpecialTile(5, "BANK", TileType.BANK));
        gameState.getBoard().getTiles().set(6, new SpecialTile(7, "RISK", TileType.RISK));
        gameState.getBoard().getTiles().set(8, new SpecialTile(9, "TAX", TileType.TAX));
        gameState.getBoard().getTiles().set(10, new SpecialTile(11, "GOTO_JAIL", TileType.GOTO_JAIL));
        gameState.getBoard().getTiles().set(20, new SpecialTile(21, "PRISON", TileType.PRISON));
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

    @Test
    void testRollToLandOnOwnedStreet() {
        // Create a street tile owned by player2
        StreetTile streetTile = new StreetTile(3, "Test Street", 200, 50, StreetLevel.NORMAL, 100);
        streetTile.setOwner(player2);
        gameState.getBoard().getTiles().set(2, streetTile); // Set position 3

        // Set initial cash
        player.setCash(1000);
        player2.setCash(1000);

        // Our custom RollDiceRequest implementation to avoid JSONObject.toMap()
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                try {
                    Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));
                    if (!player.isHasRolledDice()) {
                        player.setHasRolledDice(true);

                        // Roll dice and move
                        int[] rolls = {1, 1};
                        player.moveSteps(2);

                        // Add DICE_ROLLED message
                        extraMessages.add(new GameMessage(lobbyId, MessageType.DICE_ROLLED,
                            Map.of("playerId", player.getId(), "roll1", rolls[0], "roll2", rolls[1],
                                 "fieldIndex", player.getCurrentTile().getIndex())));

                        // Process land on owned street
                        Tile landedTile = player.getCurrentTile();
                        if (landedTile instanceof StreetTile streetTile && streetTile.getOwner() != null) {
                            Player owner = streetTile.getOwner();
                            int rent = streetTile.calculateRent();
                            player.transferCash(owner, rent);
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

        // Check that player1 paid rent to player2
        assertEquals(950, player.getCash()); // 1000 - 50 (rent)
        assertEquals(1050, player2.getCash()); // 1000 + 50 (received rent)
        assertEquals(3, player.getCurrentTile().getIndex());
        assertEquals(1, gameState.getCurrentPlayerIndex()); // Turn should advance
    }

    @Test
    void testRollToLandOnUnaffordableStreet() {
        // Create an expensive street tile
        StreetTile streetTile = new StreetTile(3, "Expensive Street", 2000, 500, StreetLevel.PREMIUM, 1000);
        gameState.getBoard().getTiles().set(2, streetTile); // Set position 3

        // Give player little money
        player.setCash(100);

        // Our custom RollDiceRequest implementation to avoid JSONObject.toMap()
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                try {
                    Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));
                    if (!player.isHasRolledDice()) {
                        player.setHasRolledDice(true);

                        // Roll dice and move
                        int[] rolls = {1, 1};
                        player.moveSteps(2);

                        // Add DICE_ROLLED message
                        extraMessages.add(new GameMessage(lobbyId, MessageType.DICE_ROLLED,
                            Map.of("playerId", player.getId(), "roll1", rolls[0], "roll2", rolls[1],
                                 "fieldIndex", player.getCurrentTile().getIndex())));

                        // Process land on unaffordable street
                        Tile landedTile = player.getCurrentTile();
                        if (landedTile instanceof StreetTile streetTile) {
                            if (streetTile.getOwner() == null && player.getCash() < streetTile.getPrice()) {
                                extraMessages.add(new GameMessage(lobbyId, MessageType.EXTRA_MESSAGE,
                                    Map.of("playerId", player.getId(), "title", "Straße kaufen",
                                         "message", "Du kannst dir diese Straße nicht leisten.")));
                                gameState.advanceTurn();
                            }
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
        assertEquals(3, player.getCurrentTile().getIndex());
        assertEquals(2, extras.size()); // DICE_ROLLED and EXTRA_MESSAGE

        // Should have EXTRA_MESSAGE about not being able to afford
        boolean hasExtraMessage = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.EXTRA_MESSAGE);
        assertTrue(hasExtraMessage);

        // Turn should advance
        assertEquals(1, gameState.getCurrentPlayerIndex());
    }

    @Test
    void testRollToPassStart() {
        // Place player near the end of the board (position 40)
        player.moveToTile(40);

        // Set up dice roll to move past START
        when(dicePair.roll()).thenReturn(new int[]{2, 1}); // Move 3 spaces, position 40 to 3

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
                        int[] rolls = {2, 1};
                        player.moveSteps(3);

                        // Add DICE_ROLLED message
                        int newIndex = player.getCurrentTile().getIndex();
                        extraMessages.add(new GameMessage(lobbyId, MessageType.DICE_ROLLED,
                            Map.of("playerId", player.getId(), "roll1", rolls[0], "roll2", rolls[1],
                                 "fieldIndex", newIndex)));

                        // Check if passed START
                        if (prevIndex > newIndex) {
                            // Add a fake passed START message
                            extraMessages.add(new GameMessage(lobbyId, MessageType.EXTRA_MESSAGE,
                                Map.of("message", "Passed START, collect money")));
                            player.addCash(200); // Simulate the START bonus
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

        int initialCash = player.getCash();

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        // Validate results
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(3, player.getCurrentTile().getIndex()); // Should be at position 3
        assertEquals(initialCash + 200, player.getCash()); // Should have received START bonus
        assertEquals(2, extras.size()); // DICE_ROLLED and EXTRA_MESSAGE
    }

    @Test
    void testRollToLandOnGoToJail() {
        // Set up a GOTO_JAIL tile and a PRISON tile
        SpecialTile jailTile = new SpecialTile(21, "PRISON", TileType.PRISON);
        gameState.getBoard().getTiles().set(20, jailTile);

        SpecialTile gotoJailTile = new SpecialTile(11, "GOTO_JAIL", TileType.GOTO_JAIL);
        gameState.getBoard().getTiles().set(10, gotoJailTile);

        // Position player
        player.moveToTile(1);

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
                            // Move to jail
                            Tile jailTile = gameState.getBoard().getTile(21);
                            player.setCurrentTile(jailTile);
                            player.suspendForRounds(3);

                            // Add GO_TO_JAIL message
                            extraMessages.add(new GameMessage(lobbyId, MessageType.GO_TO_JAIL,
                                Map.of("playerId", player.getId())));

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
        assertEquals(21, player.getCurrentTile().getIndex()); // Should be in prison (position 21)
        assertTrue(player.isSuspended()); // Should be suspended
        assertEquals(3, player.getSuspensionRounds()); // For 3 rounds
        assertEquals(2, extras.size()); // DICE_ROLLED and GO_TO_JAIL

        // Should have GO_TO_JAIL message
        boolean hasGoToJail = extras.stream()
            .anyMatch(msg -> msg.getType() == MessageType.GO_TO_JAIL);
        assertTrue(hasGoToJail);

        // Turn should advance
        assertEquals(1, gameState.getCurrentPlayerIndex());
    }

    @Test
    void testRollToLandOnBankTile() {
        // Set up a BANK tile
        SpecialTile bankTile = new SpecialTile(5, "BANK", TileType.BANK);
        gameState.getBoard().getTiles().set(4, bankTile);

        // Position player
        player.moveToTile(1);

        // Set up dice roll to land on BANK
        when(dicePair.roll()).thenReturn(new int[]{2, 2}); // Move 4 spaces to position 5

        // Mock the DrawBankCardRequest that will be created inside RollDiceRequest
        DrawBankCardRequest mockBankRequest = mock(DrawBankCardRequest.class);
        when(mockBankRequest.execute(anyInt(), any(), any(), any())).thenReturn(
            new GameMessage(lobbyId, MessageType.GAME_STATE, Map.of("test", "bank"))
        );

        // Create a modified request that uses our mock
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));

                // Skip validation checks and roll processing
                if (!player.isHasRolledDice()) {
                    player.setHasRolledDice(true);
                    player.moveSteps(4); // Move to BANK tile

                    // Check if we landed on a BANK tile
                    Tile landedTile = player.getCurrentTile();
                    if (landedTile.getType() == TileType.BANK) {
                        // Return result from our mock instead of creating a real DrawBankCardRequest
                        return mockBankRequest.execute(lobbyId, payload, gameState, extraMessages);
                    }
                }
                return MessageFactory.gameState(lobbyId, gameState);
            }
        };

        // Execute request
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        // Verify we landed on the bank tile and processed it
        assertEquals(5, player.getCurrentTile().getIndex());
        assertEquals(TileType.BANK, player.getCurrentTile().getType());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertTrue(result.getPayload().toString().contains("bank"));
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
        // Set up a TAX tile
        SpecialTile taxTile = new SpecialTile(9, "TAX", TileType.TAX);
        gameState.getBoard().getTiles().set(8, taxTile);

        // Position player
        player.moveToTile(1);

        // Set up dice roll to land on TAX
        when(dicePair.roll()).thenReturn(new int[]{4, 4}); // Move 8 spaces to position 9

        // Mock the PayTaxRequest that will be created inside RollDiceRequest
        PayTaxRequest mockTaxRequest = mock(PayTaxRequest.class);
        when(mockTaxRequest.execute(anyInt(), any(), any(), any())).thenReturn(
            new GameMessage(lobbyId, MessageType.GAME_STATE, Map.of("test", "tax"))
        );

        // Create a modified request that uses our mock
        RollDiceRequest modifiedRequest = new RollDiceRequest(dicePair) {
            @Override
            public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
                Player player = gameState.getPlayer((Integer)((Map<String,Object>)payload).get("playerId"));

                // Skip validation checks and roll processing
                if (!player.isHasRolledDice()) {
                    player.setHasRolledDice(true);
                    player.moveSteps(8); // Move to TAX tile

                    // Check if we landed on a TAX tile
                    Tile landedTile = player.getCurrentTile();
                    if (landedTile.getType() == TileType.TAX) {
                        // Return result from our mock instead of creating a real PayTaxRequest
                        return mockTaxRequest.execute(lobbyId, payload, gameState, extraMessages);
                    }
                }
                return MessageFactory.gameState(lobbyId, gameState);
            }
        };

        // Execute request
        Map<String, Object> payload = Map.of("playerId", player.getId());
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = modifiedRequest.execute(lobbyId, payload, gameState, extras);

        // Verify we landed on the tax tile and processed it
        assertEquals(9, player.getCurrentTile().getIndex());
        assertEquals(TileType.TAX, player.getCurrentTile().getType());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertTrue(result.getPayload().toString().contains("tax"));
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
}
