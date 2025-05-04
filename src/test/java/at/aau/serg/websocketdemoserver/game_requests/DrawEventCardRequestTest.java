package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.RiskTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.DrawEventCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DrawEventCardRequestTest {

    private DrawEventCardRequest request;
    private GameState gameState;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new DrawEventCardRequest();
        gameState = new GameState(new GameBoard());
        player = gameState.addPlayer("TestPlayer");
    }

    @Test
    void testPlayerNotFound_returnsError() {
        DrawEventCardPayload payload = new DrawEventCardPayload(999); // ungültige ID
        GameMessage message = new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, payload);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler nicht gefunden"));
    }

    @Test
    void testNoTileSet_returnsError() {
        DrawEventCardPayload payload = new DrawEventCardPayload(player.getId());
        GameMessage message = new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, payload);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler steht auf keinem gültigen Feld"));
    }

    @Test
    void testTileWithoutCard_returnsError() {
        player.setCurrentTile(new DummyTileWithoutCard(77));
        DrawEventCardPayload payload = new DrawEventCardPayload(player.getId());
        GameMessage message = new GameMessage(MessageType.DRAW_EVENT_BANK_CARD, payload);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Keine Karte verfügbar"));
    }

    @Test
    void testDrawEventCard_successful() {
        player.setCurrentTile(new RiskTile(3)); // funktionierendes Tile für CardFactory
        DrawEventCardPayload payload = new DrawEventCardPayload(player.getId());
        GameMessage message = new GameMessage(MessageType.DRAW_EVENT_RISIKO_CARD, payload);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.EVENT_CARD_DRAWN, result.getType());

        EventCardDrawnPayload response = result.parsePayload(EventCardDrawnPayload.class);
        assertNotNull(response.getTitle());
        assertNotNull(response.getDescription());
    }

    // Dummy Tile ohne Karte (nicht BankTile oder RiskTile)
    static class DummyTileWithoutCard extends at.aau.serg.websocketdemoserver.model.board.Tile {
        public DummyTileWithoutCard(int index) {
            super(index);
        }

        @Override
        public at.aau.serg.websocketdemoserver.model.board.TileType getType() {
            return at.aau.serg.websocketdemoserver.model.board.TileType.TAX;
        }
    }
}
