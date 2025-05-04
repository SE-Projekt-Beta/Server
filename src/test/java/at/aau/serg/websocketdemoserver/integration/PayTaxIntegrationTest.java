package at.aau.serg.websocketdemoserver.integration;


import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.SpecialTile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PayTaxRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PayTaxIntegrationTest {

    @Test
    void testPlayerPaysVermoegensabgabe() {
        GameBoard board = new GameBoard();
        GameState state = new GameState(board);
        PayTaxRequest request = new PayTaxRequest();

        Player player = state.addPlayer("Eva");
        player.setCash(1000);
        player.setCurrentTile(new SpecialTile(33, "Vermögensabgabe", TileType.TAX));

        PayTaxPayload taxPayload = new PayTaxPayload();
        taxPayload.setPlayerId(player.getId());

        GameMessage message = new GameMessage(MessageType.PAY_TAX, taxPayload);
        GameMessage result = request.execute(state, message);

        assertEquals(MessageType.TAX_PAID, result.getType());

        PayTaxPayload response = result.parsePayload(PayTaxPayload.class);
        assertEquals(250, response.getAmount()); // 25 % von 1000
        assertEquals(750, response.getNewCash());
        assertEquals(player.getId(), response.getPlayerId());
    }
}
