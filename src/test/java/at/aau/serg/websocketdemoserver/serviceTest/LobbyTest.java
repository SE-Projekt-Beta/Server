package at.aau.serg.websocketdemoserver.serviceTest;

import at.aau.serg.websocketdemoserver.model.Player;
import at.aau.serg.websocketdemoserver.service.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    private Lobby lobby;

    @BeforeEach
    void setup() {
        lobby = new Lobby();
    }

    @Test
    void testAddPlayerCreatesNewPlayer() {
        Player p = lobby.addPlayer("Alice");
        assertNotNull(p.getId());
        assertEquals("Alice", p.getUsername());
    }

    @Test
    void testAddSamePlayerReturnsSameInstance() {
        Player p1 = lobby.addPlayer("Bob");
        Player p2 = lobby.addPlayer("Bob");
        assertEquals(p1.getId(), p2.getId());
    }

    @Test
    void testGetPlayersReturnsUnmodifiableList() {
        lobby.addPlayer("Alice");
        List<Player> players = lobby.getPlayers();
        assertEquals(1, players.size());
        assertThrows(UnsupportedOperationException.class, () -> players.add(new Player("Hacker")));
    }

    @Test
    void testIsReadyToStartRequiresTwoPlayers() {
        assertFalse(lobby.isReadyToStart());
        lobby.addPlayer("A");
        assertFalse(lobby.isReadyToStart());
        lobby.addPlayer("B");
        assertTrue(lobby.isReadyToStart());
    }

    @Test
    void testClearLobbyRemovesAllPlayers() {
        lobby.addPlayer("A");
        lobby.clearLobby();
        assertEquals(0, lobby.getPlayers().size());
    }
}
