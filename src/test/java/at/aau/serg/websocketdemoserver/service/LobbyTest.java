package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.PlayerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    private Lobby lobby;

    @BeforeEach
    void setup() {
        lobby = new Lobby();
    }

    @Test
    void testAddPlayerCreatesNewPlayer() {
        PlayerDTO player = lobby.addPlayer("Alice");

        assertNotNull(player);
        assertEquals(1, player.getId());
        assertEquals("Alice", player.getNickname());

        List<PlayerDTO> players = lobby.getPlayers();
        assertEquals(1, players.size());
        assertEquals("Alice", players.get(0).getNickname());
    }

    @Test
    void testAddSamePlayerReturnsSameInstance() {
        PlayerDTO p1 = lobby.addPlayer("Bob");
        PlayerDTO p2 = lobby.addPlayer("bob"); // Groß-/Kleinschreibung ignorieren

        assertEquals(p1.getId(), p2.getId());
        assertEquals(1, lobby.getPlayers().size());
    }

    @Test
    void testGetPlayersIsUnmodifiable() {
        lobby.addPlayer("Charlie");
        List<PlayerDTO> players = lobby.getPlayers();

        assertThrows(UnsupportedOperationException.class, () -> players.add(new PlayerDTO(99, "Hacker")));
    }

    @Test
    void testIsReadyToStartNotEnoughPlayers() {
        assertFalse(lobby.isReadyToStart());

        lobby.addPlayer("Daisy");
        assertFalse(lobby.isReadyToStart());
    }

    @Test
    void testIsReadyToStartEnoughPlayers() {
        lobby.addPlayer("Eve");
        lobby.addPlayer("Frank");

        assertTrue(lobby.isReadyToStart());
    }

    @Test
    void testClearRemovesAllPlayers() {
        lobby.addPlayer("George");
        lobby.addPlayer("Helen");

        assertEquals(2, lobby.getPlayers().size());

        lobby.clear();

        assertEquals(0, lobby.getPlayers().size());
    }
}