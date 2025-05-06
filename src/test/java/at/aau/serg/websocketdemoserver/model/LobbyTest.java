package at.aau.serg.websocketdemoserver.model;

import at.aau.serg.websocketdemoserver.service.GameHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    private Lobby lobby;

    @BeforeEach
    void setUp() {
        lobby = new Lobby("InitialRoom");
    }

    @Test
    void testConstructorInitializesFields() {
        // ID and name
        assertNotNull(lobby.getId(), "Lobby ID should be generated");
        assertEquals("InitialRoom", lobby.getName(), "Lobby name should match constructor arg");

        // GameState and GameHandler
        GameState state = lobby.getGameState();
        assertNotNull(state, "GameState must be non-null");
        GameHandler handler = lobby.getGameHandler();
        assertNotNull(handler, "GameHandler must be non-null");

        // The handler must be wired to the same GameState
        assertSame(state, handler.getGameState(),
                "GameHandler should wrap the same GameState instance");
    }

    @Test
    void testSetName() {
        lobby.setName("NewRoomName");
        assertEquals("NewRoomName", lobby.getName(),
                "setName should update the lobby's name");
    }

    @Test
    void testPlayerIdManagement() {
        Set<String> players = lobby.getPlayerIds();
        assertTrue(players.isEmpty(), "New lobby should start with no players");

        // Add a player
        lobby.addPlayer("player1");
        assertTrue(players.contains("player1"),
                "addPlayer should add the ID to the set");
        assertEquals(1, players.size(),
                "Adding one player should result in size 1");

        // Adding the same ID again should not duplicate
        lobby.addPlayer("player1");
        assertEquals(1, players.size(),
                "Duplicate addPlayer calls should not increase size");

        // Remove an existing player
        lobby.removePlayer("player1");
        assertFalse(players.contains("player1"),
                "removePlayer should remove the ID");
        assertTrue(players.isEmpty(),
                "After removal, set should be empty");

        // Removing a non-existent ID is a no-op
        assertDoesNotThrow(() -> lobby.removePlayer("ghost"),
                "removePlayer on unknown ID should not throw");
    }

    @Test
    void testIdsAreUnique() {
        Lobby another = new Lobby("AnotherRoom");
        assertNotEquals(lobby.getId(), another.getId(),
                "Each Lobby should generate a unique ID");
    }
}
