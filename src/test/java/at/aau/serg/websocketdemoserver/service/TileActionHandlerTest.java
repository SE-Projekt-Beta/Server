package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileActionHandlerTest {

    private TileActionHandler handler;
    private GameBoard board;

    @BeforeEach
    void setUp() {
        handler = new TileActionHandler(new EventCardService(new BankCardDeck(), new RiskCardDeck()));
        board = new GameBoard();
    }

    @Test
    void testHandleRiskTile() {
        Player player = new Player("TestPlayer", board);
        Tile tile = new RiskTile(3);

        GameMessage result = handler.handleTileLanding(player, tile);

        assertNotNull(result);
        assertEquals("DRAW_EVENT_RISIKO_CARD", result.getType().name());
    }

    @Test
    void testHandleBankTile() {
        Player player = new Player("TestPlayer", board);
        Tile tile = new BankTile(9);

        GameMessage result = handler.handleTileLanding(player, tile);

        assertNotNull(result);
        assertEquals("DRAW_EVENT_BANK_CARD", result.getType().name());
    }


    @Test
    void testHandleStreetTile_FreeTile() {
        Player player = new Player("TestPlayer", board);
        StreetTile street = new StreetTile(5, "Teststraße", 300, 60, StreetLevel.NORMAL, 60);

        GameMessage result = handler.handleTileLanding(player, street);

        assertNotNull(result);
        assertEquals("CAN_BUY_PROPERTY", result.getType().name());
    }

    @Test
    void testHandleStreetTile_PayRent() {
        Player player = new Player("TestPlayer", board);
        StreetTile street = new StreetTile(5, "Teststraße", 300, 60, StreetLevel.NORMAL, 60);

        Player owner = new Player("Owner", board);
        street.setOwner(owner);

        GameMessage result = handler.handleTileLanding(player, street);

        assertNotNull(result);
        assertEquals("MUST_PAY_RENT", result.getType().name());
    }

    @Test
    void testHandleSpecialTile() {
        Player player = new Player("TestPlayer", board);
        Tile tile = new SpecialTile(21, "Sondersteuer");

        GameMessage result = handler.handleTileLanding(player, tile);

        assertNotNull(result);
        assertEquals("SKIPPED", result.getType().name());
    }

    @Test
    void testHandleNullPlayer() {
        Tile tile = new RiskTile(3);
        GameMessage result = handler.handleTileLanding(null, tile);

        assertNotNull(result);
        assertEquals("ERROR", result.getType().name());
    }

    @Test
    void testHandleNullTile() {
        Player player = new Player("TestPlayer", board);
        GameMessage result = handler.handleTileLanding(player, null);

        assertNotNull(result);
        assertEquals("ERROR", result.getType().name());
    }
}