package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.JailTile;
import at.aau.serg.websocketdemoserver.model.cards.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DrawRiskCardRequestTest {

    private GameState gameState;
    private Player player;
    private JailTile jailTile;
    private List<GameMessage> extraMessages;
    private Map<String, Object> payload;

    @BeforeEach
    void setUp() {
        GameBoard board = new GameBoard();
        gameState = new GameState();

        player = new Player("Tester", board);
        player.setCash(500);
        gameState.startGame(List.of(player));

        jailTile = new JailTile(10);
        extraMessages = new ArrayList<>();
        payload = new HashMap<>();
        payload.put("playerId", player.getId());
    }


    @Test
    void testCashCardPositive() {
        CashRiskCard card = new CashRiskCard(1, "Bonus", "Du bekommst Geld", 200);
        RiskCardDeck deck = mock(RiskCardDeck.class);
        when(deck.drawCard()).thenReturn(card);

        DrawRiskCardRequest request = new DrawRiskCardRequest(deck, jailTile);
        GameMessage result = request.execute(1, payload, gameState, extraMessages);

        assertEquals(700, player.getCash());
        assertEquals(MessageType.GAME_STATE, result.getType());
        assertTrue(extraMessages.stream().anyMatch(m -> m.getType() == MessageType.DRAW_RISK_CARD));
    }

    @Test
    void testCashCardNegativeBankrupt() {
        player.setCash(50);
        CashRiskCard card = new CashRiskCard(2, "Strafe", "Du verlierst Geld", -100);
        RiskCardDeck deck = mock(RiskCardDeck.class);
        when(deck.drawCard()).thenReturn(card);

        DrawRiskCardRequest request = new DrawRiskCardRequest(deck, jailTile);
        GameMessage result = request.execute(1, payload, gameState, extraMessages);

        assertFalse(player.isAlive());
        assertTrue(extraMessages.stream().anyMatch(m -> m.getType() == MessageType.PLAYER_LOST));
    }

    @Test
    void testEscapeCard() {
        EscapeRiskCard card = new EscapeRiskCard(3, "Freiheit", "Du darfst raus");
        RiskCardDeck deck = mock(RiskCardDeck.class);
        when(deck.drawCard()).thenReturn(card);

        DrawRiskCardRequest request = new DrawRiskCardRequest(deck, jailTile);
        GameMessage result = request.execute(1, payload, gameState, extraMessages);

        assertTrue(player.hasEscapeCard());
        assertTrue(extraMessages.stream().anyMatch(m -> m.getType() == MessageType.PLAYER_OUT_OF_JAIL_CARD));
    }


    @Test
    void testGoToJailWithEscapeCard() {
        player.setEscapeCard(true);
        GoToJailRiskCard card = new GoToJailRiskCard(5, "Geh ins Gefängnis", "Aber du hast eine Karte");
        RiskCardDeck deck = mock(RiskCardDeck.class);
        when(deck.drawCard()).thenReturn(card);

        DrawRiskCardRequest request = new DrawRiskCardRequest(deck, jailTile);
        GameMessage result = request.execute(1, payload, gameState, extraMessages);

        assertFalse(player.hasEscapeCard());
        assertNull(player.getCurrentTile());
        assertEquals(0, player.getSuspensionRounds());
    }

    @Test
    void testInvalidPlayerId() {
        Map<String, Object> invalidPayload = new HashMap<>();
        invalidPayload.put("playerId", 999);  // Nicht existierender Spieler

        RiskCardDeck deck = mock(RiskCardDeck.class);
        when(deck.drawCard()).thenReturn(new CashRiskCard(6, "Dummy", "irrelevant", 50));

        DrawRiskCardRequest request = new DrawRiskCardRequest(deck, jailTile);
        GameMessage result = request.execute(1, invalidPayload, gameState, extraMessages);

        assertEquals(MessageType.ERROR, result.getType());
    }
}
