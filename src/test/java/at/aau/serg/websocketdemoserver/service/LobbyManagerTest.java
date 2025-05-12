package at.aau.serg.websocketdemoserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerTest {

    private LobbyManager lobbyManager;

    @BeforeEach
    void setUp() {
        lobbyManager = new LobbyManager();
    }

    @Test
    void testCreateLobbyAndGetLobby() {
        int id = lobbyManager.createLobby("TestLobby");
        Lobby lobby = lobbyManager.getLobby(id);

        assertNotNull(lobby);
        assertEquals("TestLobby", lobby.getLobbyName());
    }

    @Test
    void testGetLobbyIds() {
        int id1 = lobbyManager.createLobby("Lobby1");
        int id2 = lobbyManager.createLobby("Lobby2");

        List<Integer> ids = lobbyManager.getLobbyIds();
        assertTrue(ids.contains(id1));
        assertTrue(ids.contains(id2));
        assertEquals(2, ids.size());
    }

    @Test
    void testRemoveLobby() {
        int id = lobbyManager.createLobby("TempLobby");
        assertNotNull(lobbyManager.getLobby(id));

        boolean removed = lobbyManager.removeLobby(id);
        assertTrue(removed);
        assertNull(lobbyManager.getLobby(id));
    }

    @Test
    void testRemoveNonexistentLobbyReturnsFalse() {
        assertFalse(lobbyManager.removeLobby(999));
    }
}
