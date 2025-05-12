package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrawBankCardRequestTest {

    private DrawBankCardRequest request;
    private GameState gameState;
    private Player player;
    private BankCardDeck mockDeck;
    private int lobbyId;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();
        player = new Player("TestPlayer", board);
        player.setCash(100);
        gameState.startGame(List.of(player));
        lobbyId = 1;

        mockDeck = mock(BankCardDeck.class);
        request = new DrawBankCardRequest(mockDeck);
    }

    private Map<String, Object> buildPayload(int playerId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", playerId);
        return payload;
    }

    @Test
    void testValidCardDrawPositiveAmount() {
        BankCard mockCard = new BankCard(1, "Bonus", "Du erhältst Geld", 100);
        when(mockDeck.drawCard()).thenReturn(mockCard);

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, buildPayload(player.getId()), gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(200, player.getCash());

        assertEquals(1, extras.size());
        assertEquals(MessageType.DRAW_BANK_CARD, extras.get(0).getType());
    }

    @Test
    void testValidCardDrawNegativeAmountNoBankrupt() {
        BankCard mockCard = new BankCard(2, "Steuer", "Du verlierst Geld", -50);
        when(mockDeck.drawCard()).thenReturn(mockCard);

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, buildPayload(player.getId()), gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(50, player.getCash());

        assertEquals(1, extras.size());
        assertEquals(MessageType.DRAW_BANK_CARD, extras.get(0).getType());
    }

    @Test
    void testCardDrawCausesBankrupt() {
        BankCard mockCard = new BankCard(3, "Strafe", "Zu hohe Schulden", -200);
        when(mockDeck.drawCard()).thenReturn(mockCard);

        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, buildPayload(player.getId()), gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertFalse(player.isAlive());

        assertEquals(2, extras.size());
        assertEquals(MessageType.DRAW_BANK_CARD, extras.get(0).getType());
        assertEquals(MessageType.PLAYER_LOST, extras.get(1).getType());
    }

    @Test
    void testInvalidPlayer() {
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, buildPayload(999), gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testExceptionHandling() {
        Map<String, Object> broken = new HashMap<>(); // missing playerId
        List<GameMessage> extras = new ArrayList<>();
        GameMessage result = request.execute(lobbyId, broken, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Ziehen"));
    }
}
