package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.GoToJailPayload;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.GoToJailTile;
import at.aau.serg.websocketdemoserver.model.board.JailTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.GoToJailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoToJailRequestTest {

    private GameState gameState;
    private Player player;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        player = new Player("TestPlayer", gameState.getBoard());
        gameState.addPlayer(player);
    }

    @Test
    void testExecute_validPlayer_goesToJail() {
        // given
        GoToJailPayload payload = new GoToJailPayload(player.getId(), 0, 0, "");
        GameMessage message = new GameMessage(MessageType.GO_TO_JAIL, payload);

        // when
        GoToJailRequest request = new GoToJailRequest();
        GameMessage result = request.execute(gameState, message);

        // then
        assertEquals(MessageType.GO_TO_JAIL, result.getType());

        GoToJailPayload response = result.parsePayload(GoToJailPayload.class);
        assertEquals(player.getId(), response.getPlayerId());
        assertEquals(31, response.getJailPosition());
        assertEquals(3, response.getRoundsInJail());
        assertNotNull(response.getReason());

        Tile jailTile = gameState.getBoard().getTile(31);
        assertEquals(jailTile, player.getCurrentTile());
        assertTrue(player.isSuspended());
    }

    @Test
    void testExecute_playerNotFound_returnsErrorMessage() {
        // ID = 999 does not exist
        GoToJailPayload payload = new GoToJailPayload(999, 0, 0, "");
        GameMessage message = new GameMessage(MessageType.GO_TO_JAIL, payload);

        GameMessage result = new GoToJailRequest().execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }
}
