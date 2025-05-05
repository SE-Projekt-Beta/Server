package at.aau.serg.websocketdemoserver.model.state;

import at.aau.serg.websocketdemoserver.dto.GameEndedPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameStateTest {

    private GameState state;

    @BeforeEach
    void setup() {
        state = new GameState();
    }

    @Test
    void testAddAndGetPlayer() {
        Player player = mock(Player.class);
        when(player.getId()).thenReturn(1);
        state.addPlayer(player);
        assertEquals(player, state.getPlayer(1));
        assertTrue(state.getPlayers().contains(player));
    }

    @Test
    void testRemovePlayer() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        when(p1.getId()).thenReturn(1);
        when(p2.getId()).thenReturn(2);

        state.addPlayer(p1);
        state.addPlayer(p2);
        state.removePlayer(p1);

        assertFalse(state.getPlayers().contains(p1));
        assertTrue(state.getPlayers().contains(p2));
    }

    @Test
    void testAdvanceTurnWrapsAround() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        state.addPlayer(p1);
        state.addPlayer(p2);

        assertEquals(p1, state.getCurrentPlayer());
        state.advanceTurn();
        assertEquals(p2, state.getCurrentPlayer());
        state.advanceTurn();
        assertEquals(p1, state.getCurrentPlayer());
    }

    @Test
    void testPeekNextPlayer() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        state.addPlayer(p1);
        state.addPlayer(p2);

        assertEquals(p2, state.peekNextPlayer());
        state.advanceTurn();
        assertEquals(p1, state.peekNextPlayer());
    }

    @Test
    void testStartGameInitializesPositions() {
        Tile startTile = mock(Tile.class);

        try (MockedStatic<GameBoard> mocked = mockStatic(GameBoard.class)) {
            GameBoard board = mock(GameBoard.class);
            when(board.getTile(1)).thenReturn(startTile);
            mocked.when(GameBoard::get).thenReturn(board);

            Player p1 = mock(Player.class);
            Player p2 = mock(Player.class);
            state.addPlayer(p1);
            state.addPlayer(p2);

            state.startGame();

            verify(p1).moveToTile(1, board);
            verify(p2).moveToTile(1, board);
            assertTrue(state.isGameStarted());
            assertNotNull(state.getCurrentPlayer());
        }
    }

    @Test
    void testOwnedPropertiesOnlyReturnsMatchingOwner() {
        Player player = mock(Player.class);
        StreetTile ownedTile = mock(StreetTile.class);
        when(ownedTile.getOwner()).thenReturn(player);
        StreetTile otherTile = mock(StreetTile.class);
        when(otherTile.getOwner()).thenReturn(null);

        try (MockedStatic<GameBoard> mocked = mockStatic(GameBoard.class)) {
            GameBoard board = mock(GameBoard.class);
            when(board.getTiles()).thenReturn(List.of(ownedTile, otherTile));
            mocked.when(GameBoard::get).thenReturn(board);

            List<StreetTile> result = state.getOwnedProperties(player);
            assertEquals(1, result.size());
            assertEquals(ownedTile, result.get(0));
        }
    }

    @Test
    void testGameEndTriggeredWithOnePlayer() {
        Player winner = mock(Player.class);
        when(winner.getNickname()).thenReturn("Thomas");
        when(winner.calculateWealth()).thenReturn(500);

        state.addPlayer(winner);

        GameMessage result = state.checkForGameEnd();
        assertNotNull(result);
        assertEquals(MessageType.END_GAME, result.getType());

        GameEndedPayload payload = (GameEndedPayload) result.getPayload();
        assertEquals(1, payload.getRanking().size());
        assertEquals("Thomas", payload.getRanking().get(0).getNickname());
        assertEquals(500, payload.getRanking().get(0).getWealth());
    }

    @Test
    void testGameEndReturnsNullWithMultiplePlayers() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        state.addPlayer(p1);
        state.addPlayer(p2);
        assertNull(state.checkForGameEnd());
    }

    @Test
    void testResetGameClearsPlayersAndFlags() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        state.addPlayer(p1);
        state.addPlayer(p2);

        try (MockedStatic<GameBoard> mocked = mockStatic(GameBoard.class)) {
            GameBoard board = mock(GameBoard.class);
            Tile startTile = mock(Tile.class);
            when(board.getTile(1)).thenReturn(startTile);
            mocked.when(GameBoard::get).thenReturn(board);
            state.startGame();
        }

        state.resetGame();
        verify(p1).resetProperties();
        verify(p2).resetProperties();
        assertTrue(state.getPlayers().isEmpty());
        assertFalse(state.isGameStarted());
        assertEquals(0, state.getCurrentPlayerIndex());
    }

    @Test
    void testSetPlayersOverridesList() {
        Player p1 = mock(Player.class);
        when(p1.getId()).thenReturn(1);
        state.setPlayers(List.of(p1));
        assertEquals(1, state.getPlayers().size());
        assertEquals(p1, state.getCurrentPlayer());
    }
}
