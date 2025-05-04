package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLostPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerLostRequestTest {

    private GameState gameState;
    private Player player1;
    private Player player2;
    private PlayerLostRequest request;

    @BeforeEach
    void setup() {
        gameState = new GameState(new GameBoard());
        player1 = gameState.addPlayer("Alice");
        player2 = gameState.addPlayer("Bob");
        request = new PlayerLostRequest();
    }

    @Test
    void testPlayerNotFound() {
        PlayerLostPayload payload = new PlayerLostPayload();
        payload.setPlayerId(-1);
        GameMessage msg = new GameMessage(MessageType.PLAYER_LOST, payload);

        GameMessage result = request.execute(gameState, msg);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPlayerLost_GameNotOver() {
        gameState.addPlayer("Clara"); // ➕ hier hinzufügen
        PlayerLostPayload payload = new PlayerLostPayload();
        payload.setPlayerId(player1.getId());
        GameMessage msg = new GameMessage(MessageType.PLAYER_LOST, payload);

        GameMessage result = request.execute(gameState, msg);

        assertEquals(MessageType.PLAYER_LOST, result.getType());
        PlayerLostPayload response = result.parsePayload(PlayerLostPayload.class);
        assertEquals(player1.getId(), response.getPlayerId());
        assertEquals(player1.getNickname(), response.getNickname());
        assertFalse(response.isGameOver()); // ✅ funktioniert jetzt
        assertNull(gameState.getPlayer(player1.getId()));
    }


    @Test
    void testPlayerLost_GameOver() {
        // Nur 1 Spieler vorhanden → nach Verlust Spiel vorbei
        gameState = new GameState(new GameBoard());
        player1 = gameState.addPlayer("Solo");

        PlayerLostPayload payload = new PlayerLostPayload();
        payload.setPlayerId(player1.getId());
        GameMessage msg = new GameMessage(MessageType.PLAYER_LOST, payload);

        GameMessage result = request.execute(gameState, msg);

        assertEquals(MessageType.PLAYER_LOST, result.getType());
        PlayerLostPayload response = result.parsePayload(PlayerLostPayload.class);
        assertEquals(player1.getId(), response.getPlayerId());
        assertEquals(player1.getNickname(), response.getNickname());
        assertTrue(response.isGameOver());
        assertNull(gameState.getPlayer(player1.getId()));
    }
}
