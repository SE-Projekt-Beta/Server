package at.aau.serg.websocketdemoserver.integration;


import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.MovePlayerRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RollDiceIntegrationTest {

    @Test
    void testRollDiceMovesPlayerToUnownedStreet() {
        GameBoard board = new GameBoard();
        GameState state = new GameState(board);
        MovePlayerRequest request = new MovePlayerRequest();

        Player player = state.addPlayer("Max");
        player.setPosition(1); // Ausgangsposition auf Feld 1
        int steps = 1; // Feld 2 = kaufbares StreetTile

        MovePlayerPayload payload = new MovePlayerPayload();
        payload.setPlayerId(player.getId());
        payload.setDice(steps);
        GameMessage message = new GameMessage(MessageType.ROLL_DICE, payload);

        GameMessage result = request.execute(state, message);

        assertEquals(MessageType.CAN_BUY_PROPERTY, result.getType());

        BuyPropertyPayload response = result.parsePayload(BuyPropertyPayload.class);
        assertEquals(player.getId(), response.getPlayerId());
        assertEquals(player.getCurrentTile().getIndex(), response.getTilePosition());
    }
}
