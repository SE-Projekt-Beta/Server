package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.StartMoneyPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.StartMoneyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartMoneyRequestTest {

    private GameState gameState;
    private StartMoneyRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new StartMoneyRequest();
    }

    @Test
    void testPlayerNotFound() {
        GameMessage message = new GameMessage(MessageType.START_MONEY, 999); // ungültige ID
        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testStartMoneyGranted() {
        Player player = gameState.addPlayer("Alice");
        int playerId = player.getId();
        int oldCash = player.getCash();

        GameMessage message = new GameMessage(MessageType.START_MONEY, playerId);
        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.START_MONEY, result.getType());

        StartMoneyPayload payload = result.parsePayload(StartMoneyPayload.class);
        assertEquals(playerId, payload.getPlayerId());
        assertEquals(200, payload.getBonusAmount());
        assertEquals(oldCash + 200, payload.getNewCash());
        assertEquals(oldCash + 200, player.getCash());
    }
}
