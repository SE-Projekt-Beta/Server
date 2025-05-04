package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLobbyEntry;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyUpdateRequestTest {

    private GameState gameState;
    private LobbyUpdateRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new LobbyUpdateRequest();
    }

    @Test
    void testExecuteWithNoPlayers() {
        LobbyMessage result = request.execute(gameState, null);

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.getType());

        @SuppressWarnings("unchecked")
        List<PlayerLobbyEntry> payload = (List<PlayerLobbyEntry>) result.getPayload();

        assertTrue(payload.isEmpty());
    }

    @Test
    void testExecuteWithPlayers() {
        gameState.addPlayer("Alice");
        gameState.addPlayer("Bob");

        LobbyMessage result = request.execute(gameState, null);

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.getType());

        @SuppressWarnings("unchecked")
        List<PlayerLobbyEntry> payload = (List<PlayerLobbyEntry>) result.getPayload();

        assertEquals(2, payload.size());
        assertEquals("Alice", payload.get(0).getNickname());
        assertEquals("Bob", payload.get(1).getNickname());
    }
}
