package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.JailCardHandlingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JailCardHandlingRequestTest {

    private GameState gameState;
    private Player player;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        player = new Player("Eva", gameState.getBoard());
        gameState.addPlayer(player);
    }

    @Test
    void testExecute_withEscapeCard_success() {
        player.setEscapeCard(true);
        player.suspendForRounds(3);

        PlayerOutOfJailCardPayload payload = new PlayerOutOfJailCardPayload(
                player.getId(), "");
        GameMessage message = new GameMessage(MessageType.PLAYER_OUT_OF_JAIL_CARD, payload);

        GameMessage result = new JailCardHandlingRequest().execute(gameState, message);

        assertEquals(MessageType.PLAYER_OUT_OF_JAIL_CARD, result.getType());

        PlayerOutOfJailCardPayload response = result.parsePayload(PlayerOutOfJailCardPayload.class);
        assertEquals(player.getId(), response.getPlayerId());
        assertTrue(response.getNickname().contains("Gefängnis-frei"));

        assertFalse(player.hasEscapeCard());
        assertEquals(0, player.getSuspensionRounds());
    }

    @Test
    void testExecute_withoutEscapeCard_error() {
        player.setEscapeCard(false);
        player.suspendForRounds(3);

        PlayerOutOfJailCardPayload payload = new PlayerOutOfJailCardPayload(
                player.getId(), "");
        GameMessage message = new GameMessage(MessageType.PLAYER_OUT_OF_JAIL_CARD, payload);

        GameMessage result = new JailCardHandlingRequest().execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("keine 'Gefängnis-frei'"));
    }

    @Test
    void testExecute_playerNotFound_error() {
        PlayerOutOfJailCardPayload payload = new PlayerOutOfJailCardPayload(999, "");
        GameMessage message = new GameMessage(MessageType.PLAYER_OUT_OF_JAIL_CARD, payload);

        GameMessage result = new JailCardHandlingRequest().execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }
}
