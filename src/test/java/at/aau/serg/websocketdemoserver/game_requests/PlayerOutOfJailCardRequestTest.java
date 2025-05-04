package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PlayerOutOfJailCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerOutOfJailCardRequestTest {

    private GameState gameState;
    private Player player;
    private PlayerOutOfJailCardRequest request;

    @BeforeEach
    void setup() {
        gameState = new GameState(new GameBoard());
        player = gameState.addPlayer("Max");
        request = new PlayerOutOfJailCardRequest();
    }

    @Test
    void testPlayerNotFound() {
        PlayerOutOfJailCardPayload payload = new PlayerOutOfJailCardPayload(-1, null);
        GameMessage message = new GameMessage(MessageType.PLAYER_OUT_OF_JAIL_CARD, payload);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPlayerReceivesEscapeCard() {
        PlayerOutOfJailCardPayload payload = new PlayerOutOfJailCardPayload(player.getId(), null);
        GameMessage message = new GameMessage(MessageType.PLAYER_OUT_OF_JAIL_CARD, payload);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.PLAYER_OUT_OF_JAIL_CARD, result.getType());
        assertTrue(player.hasEscapeCard());

        PlayerOutOfJailCardPayload response = result.parsePayload(PlayerOutOfJailCardPayload.class);
        assertEquals(player.getId(), response.getPlayerId());
        assertEquals(player.getNickname(), response.getNickname());
    }
}
