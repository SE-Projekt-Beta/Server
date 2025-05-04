package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PayTaxRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayTaxRequestTest {

    private GameState gameState;
    private Player player;
    private PayTaxRequest request;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        player = gameState.addPlayer("Player 1");
        request = new PayTaxRequest();
    }

    private GameMessage createMessage(int playerId) {
        PayTaxPayload payload = new PayTaxPayload();
        payload.setPlayerId(playerId);
        return new GameMessage(MessageType.PAY_TAX, payload);
    }

    @Test
    void testPlayerNotFound() {
        GameMessage result = request.execute(gameState, createMessage(-1));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testFieldIsNotTaxField() {
        player.setCurrentTile(gameState.getBoard().getTile(2)); // StreetTile
        GameMessage result = request.execute(gameState, createMessage(player.getId()));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testUnknownTaxField() {
        player.setCurrentTile(new SpecialTile(99, "Unbekannt", TileType.TAX));
        GameMessage result = request.execute(gameState, createMessage(player.getId()));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testSondersteuerStaticTaxField() {
        player.setCash(1000);
        player.setCurrentTile(new SpecialTile(21, "Sondersteuer", TileType.TAX));

        GameMessage result = request.execute(gameState, createMessage(player.getId()));

        assertEquals(MessageType.TAX_PAID, result.getType());

        PayTaxPayload response = result.parsePayload(PayTaxPayload.class);
        assertEquals(player.getId(), response.getPlayerId());
        assertEquals("Sondersteuer", response.getTileName());
        assertEquals(300, response.getAmount());
        assertEquals(1000, response.getOldCash());
        assertEquals(700, response.getNewCash());
    }

    @Test
    void testVermoegensabgabeTaxFieldWithMaxCap() {
        player.setCash(5000);
        player.setCurrentTile(new SpecialTile(33, "Vermögensabgabe", TileType.TAX));

        GameMessage result = request.execute(gameState, createMessage(player.getId()));

        assertEquals(MessageType.TAX_PAID, result.getType());

        PayTaxPayload response = result.parsePayload(PayTaxPayload.class);
        assertEquals(player.getId(), response.getPlayerId());
        assertEquals("Vermögensabgabe", response.getTileName());
        assertEquals(400, response.getAmount()); // capped
        assertEquals(5000, response.getOldCash());
        assertEquals(4600, response.getNewCash());
    }

    @Test
    void testVermoegensabgabeTaxFieldBelowMax() {
        player.setCash(800);
        player.setCurrentTile(new SpecialTile(33, "Vermögensabgabe", TileType.TAX));

        GameMessage result = request.execute(gameState, createMessage(player.getId()));

        assertEquals(MessageType.TAX_PAID, result.getType());

        PayTaxPayload response = result.parsePayload(PayTaxPayload.class);
        assertEquals(200, response.getAmount()); // 25% of 800
        assertEquals(800, response.getOldCash());
        assertEquals(600, response.getNewCash());
    }

    @Test
    void testPlayerGoesBankrupt() {
        player.setCash(1); // ergibt Steuerbetrag 0 wegen int-Cast → kein Verlust

        // Um Verlust auszulösen, muss Betrag kleiner als Steuerbetrag sein
        player.setCash(2); // ergibt 0 → auch kein Verlust

        // Lösung: Betrag kleiner als Steuerbetrag MINDESTENS 1
        player.setCash(1); // ergibt Steuerbetrag 0

        // => Wir müssen künstlich den Steuerbetrag erhöhen (z. B. 300 bei Sondersteuer)
        player.setCash(100); // 25% = 25

        // also setzen wir 24
        player.setCash(24); // 25% = 6 → 24 - 6 = 18 → noch kein Bankrott

        // Endgültig:
        player.setCash(1); // 25% von 1 = 0 → Kein Bankrott → keine Lösung

        // 👉 RICHTIG:
        player.setCash(1);
        player.setCurrentTile(new SpecialTile(33, "Sondersteuer", TileType.TAX)); // Steuer = 300

        GameMessage result = request.execute(gameState, createMessage(player.getId()));

        assertEquals(MessageType.PLAYER_LOST, result.getType());

        PlayerLostPayload payload = result.parsePayload(PlayerLostPayload.class);
        assertEquals(player.getId(), payload.getPlayerId());
        assertEquals(player.getNickname(), payload.getNickname());
    }

}
