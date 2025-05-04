package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLobbyEntry;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.lobby_request.JoinLobbyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JoinLobbyRequestTest {

    private GameState gameState;
    private JoinLobbyRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new JoinLobbyRequest();
    }

    @Test
    void testNullNickname() {
        LobbyMessage result = request.execute(gameState, null);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Nickname.", result.getPayload());
    }

    @Test
    void testNonStringParameter() {
        LobbyMessage result = request.execute(gameState, 123); // kein String
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Nickname.", result.getPayload());
    }

    @Test
    void testBlankNickname() {
        LobbyMessage result = request.execute(gameState, "   ");
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiger Nickname.", result.getPayload());
    }

    @Test
    void testValidNickname() {
        LobbyMessage result = request.execute(gameState, "Eva");

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.getType());
        assertTrue(result.getPayload() instanceof List<?>);

        @SuppressWarnings("unchecked")
        List<PlayerLobbyEntry> entries = (List<PlayerLobbyEntry>) result.getPayload();

        assertEquals(1, entries.size());
        PlayerLobbyEntry entry = entries.get(0);
        assertEquals("Eva", entry.getNickname());
        assertTrue(entry.getPlayerId() > 0);
    }

    @Test
    void testMultiplePlayers() {
        request.execute(gameState, "Anna");
        request.execute(gameState, "Ben");
        LobbyMessage result = request.execute(gameState, "Chris");

        @SuppressWarnings("unchecked")
        List<PlayerLobbyEntry> entries = (List<PlayerLobbyEntry>) result.getPayload();

        assertEquals(3, entries.size());
        assertTrue(entries.stream().anyMatch(e -> e.getNickname().equals("Anna")));
        assertTrue(entries.stream().anyMatch(e -> e.getNickname().equals("Ben")));
        assertTrue(entries.stream().anyMatch(e -> e.getNickname().equals("Chris")));
    }
}
