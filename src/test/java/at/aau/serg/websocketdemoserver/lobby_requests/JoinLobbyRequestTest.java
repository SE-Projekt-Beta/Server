package at.aau.serg.websocketdemoserver.lobby_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.lobby_request.JoinLobbyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JoinLobbyRequestTest {

    private JoinLobbyRequest request;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        request = new JoinLobbyRequest();
        gameState = mock(GameState.class);
    }

    @Test
    void testGetType() {
        assertEquals(LobbyMessageType.JOIN_LOBBY, request.getType());
    }

    @Test
    void testInvalidParameterType_returnsError() {
        LobbyMessage result = request.execute(gameState, 42); // kein JoinLobbyPayload
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Ungültiges Payload-Format.", result.getPayload());
    }

    @Test
    void testEmptyNickname_returnsError() {
        JoinLobbyPayload payload = new JoinLobbyPayload();
        payload.setNickname("  ");

        LobbyMessage result = request.execute(gameState, payload);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Nickname fehlt.", result.getPayload());
    }

    @Test
    void testDuplicateNickname_returnsError() {
        Player p1 = mock(Player.class);
        when(p1.getNickname()).thenReturn("Thomas");

        when(gameState.getPlayers()).thenReturn(List.of(p1));

        JoinLobbyPayload payload = new JoinLobbyPayload();
        payload.setNickname("thomas"); // case-insensitive match

        LobbyMessage result = request.execute(gameState, payload);
        assertEquals(LobbyMessageType.ERROR, result.getType());
        assertEquals("Nickname bereits vergeben.", result.getPayload());
    }

    @Test
    void testValidNickname_returnsLobbyUpdate() {
        Player p1 = mock(Player.class);
        when(p1.getId()).thenReturn(1);
        when(p1.getNickname()).thenReturn("Eva");

        Player p2 = mock(Player.class);
        when(p2.getId()).thenReturn(2);
        when(p2.getNickname()).thenReturn("Max");

        when(gameState.getPlayers()).thenReturn(List.of(p1, p2));

        JoinLobbyPayload payload = new JoinLobbyPayload();
        payload.setNickname("Tom");

        LobbyMessage result = request.execute(gameState, payload);

        assertEquals(LobbyMessageType.LOBBY_UPDATE, result.getType());
        assertTrue(result.getPayload() instanceof LobbyUpdatePayload);

        LobbyUpdatePayload lobbyUpdate = (LobbyUpdatePayload) result.getPayload();
        assertEquals(2, lobbyUpdate.getPlayers().size());

        assertEquals("Eva", lobbyUpdate.getPlayers().get(0).getNickname());
        assertEquals(1, lobbyUpdate.getPlayers().get(0).getPlayerId());

        assertEquals("Max", lobbyUpdate.getPlayers().get(1).getNickname());
        assertEquals(2, lobbyUpdate.getPlayers().get(1).getPlayerId());
    }
}
