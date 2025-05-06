package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerTest {

    private LobbyManager lobbyManager;

    @BeforeEach
    void setUp() {
        lobbyManager = new LobbyManager();
    }

    @Test
    void testCreateAndGetLobby() {
        // Create a new lobby
        Lobby lobby = lobbyManager.createLobby("Test Room");
        assertNotNull(lobby, "createLobby should return a Lobby instance");
        assertEquals("Test Room", lobby.getName(), "Lobby name must match the argument");

        // getLobby should find it by its generated ID
        String id = lobby.getId();
        Lobby fetched = lobbyManager.getLobby(id);
        assertSame(lobby, fetched, "getLobby must return the same instance that was created");
    }

    @Test
    void testGetNonexistentLobby() {
        // Looking up a random ID returns null
        assertNull(lobbyManager.getLobby("no-such-id"), "getLobby on unknown ID should return null");
    }

    @Test
    void testListLobbies() {
        // Create multiple lobbies
        Lobby one = lobbyManager.createLobby("Alpha");
        Lobby two = lobbyManager.createLobby("Beta");
        Lobby three = lobbyManager.createLobby("Gamma");

        Collection<Lobby> all = lobbyManager.listLobbies();
        assertEquals(3, all.size(), "There should be exactly 3 active lobbies");

        Set<String> names = all.stream()
                .map(Lobby::getName)
                .collect(Collectors.toSet());
        assertTrue(names.containsAll(Set.of("Alpha","Beta","Gamma")),
                "listLobbies must include all created lobby names");
    }

    @Test
    void testRemoveLobby() {
        // Create then remove
        Lobby lobby = lobbyManager.createLobby("Disposable");
        String id = lobby.getId();

        assertNotNull(lobbyManager.getLobby(id), "Lobby should exist before removal");
        lobbyManager.removeLobby(id);
        assertNull(lobbyManager.getLobby(id), "Lobby should be gone after removal");

        // And listLobbies no longer contains it
        assertFalse(lobbyManager.listLobbies()
                        .stream()
                        .anyMatch(l -> l.getId().equals(id)),
                "Removed lobby must not appear in listLobbies");
    }

    @Test
    void testRemoveNonexistentLobbyDoesNotThrow() {
        // Removing a non-existent ID should be a no-op
        assertDoesNotThrow(() -> lobbyManager.removeLobby("unknown-id"),
                "removeLobby on unknown ID should not throw");
    }
}
