package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PayRentPayload;
import at.aau.serg.websocketdemoserver.dto.PlayerLostPayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PayRentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayRentRequestTest {

    private GameState gameState;
    private PayRentRequest request;
    private Player payer;
    private Player owner;
    private StreetTile street;

    @BeforeEach
    void setUp() {
        gameState = new GameState(new GameBoard());
        request = new PayRentRequest();

        payer = gameState.addPlayer("Payer");
        owner = gameState.addPlayer("Owner");

        // Wir verwenden z. B. Tile 2 = StreetTile (Amtsplatz)
        street = (StreetTile) gameState.getBoard().getTile(2);
        street.setOwner(owner);
        payer.setCurrentTile(street);
    }

    private GameMessage createMessage(int fromId, int toId) {
        PayRentPayload payload = new PayRentPayload();
        payload.setFromPlayerId(fromId);
        payload.setToPlayerId(toId);
        return new GameMessage(MessageType.PAY_RENT, payload);
    }

    @Test
    void testSuccessfulRentPayment() {
        payer.setCash(1000);
        int rent = street.calculateRent();

        GameMessage result = request.execute(gameState, createMessage(payer.getId(), owner.getId()));
        assertEquals(MessageType.RENT_PAID, result.getType());

        PayRentPayload response = result.parsePayload(PayRentPayload.class);
        assertEquals(payer.getId(), response.getFromPlayerId());
        assertEquals(owner.getId(), response.getToPlayerId());
        assertEquals(rent, response.getAmount());
    }

    @Test
    void testPayerNotFound() {
        GameMessage result = request.execute(gameState, createMessage(-1, owner.getId()));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testOwnerNotFound() {
        GameMessage result = request.execute(gameState, createMessage(payer.getId(), -1));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTileIsNotStreet() {
        payer.setCurrentTile(gameState.getBoard().getTile(1)); // Startfeld
        GameMessage result = request.execute(gameState, createMessage(payer.getId(), owner.getId()));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTileHasNoOwner() {
        street.setOwner(null);
        GameMessage result = request.execute(gameState, createMessage(payer.getId(), owner.getId()));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTileOwnerMismatch() {
        Player other = gameState.addPlayer("WrongOwner");
        street.setOwner(other);
        GameMessage result = request.execute(gameState, createMessage(payer.getId(), owner.getId()));
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPayerGoesBankrupt() {
        payer.setCash(0);
        GameMessage result = request.execute(gameState, createMessage(payer.getId(), owner.getId()));
        assertEquals(MessageType.PLAYER_LOST, result.getType());

        PlayerLostPayload payload = result.parsePayload(PlayerLostPayload.class);
        assertEquals(payer.getId(), payload.getPlayerId());
        assertEquals(payer.getNickname(), payload.getNickname());
    }
}
