package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.JailTile;
import at.aau.serg.websocketdemoserver.model.cards.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrawRiskCardRequestTest {

    private DrawRiskCardRequest request;
    private GameState gameState;
    private Player player;
    private JailTile jailTile;
    private RiskCardDeck mockDeck;
    private int lobbyId;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();
        player = new Player("RiskTester", board);
        gameState.startGame(List.of(player));
        lobbyId = 1;

        jailTile = new JailTile(31);
        mockDeck = mock(RiskCardDeck.class);
        request = new DrawRiskCardRequest(mockDeck, jailTile);
    }

    private Map<String, Object> payloadForPlayer() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", player.getId());
        return payload;
    }

    @Test
    void testCashRiskCardPositive() {
        when(mockDeck.drawCard()).thenReturn(new CashRiskCard(1, "Bonus", "Du bekommst Geld", 100));
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(4000, player.getCash());
        assertEquals(1, extras.size());
        assertEquals(MessageType.DRAW_RISK_CARD, extras.get(0).getType());
    }

    @Test
    void testCashRiskCardNegativeNoBankrupt() {
        player.setCash(200);
        when(mockDeck.drawCard()).thenReturn(new CashRiskCard(2, "Strafe", "Du verlierst Geld", -100));
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertEquals(100, player.getCash());
        assertEquals(1, extras.size());
    }

    @Test
    void testCashRiskCardCausesBankrupt() {
        player.setCash(50);
        when(mockDeck.drawCard()).thenReturn(new CashRiskCard(3, "Zwangsenteignung", "Alle Ersparnisse verloren", -100));
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertFalse(player.isAlive());
        assertEquals(2, extras.size());
        assertEquals(MessageType.PLAYER_LOST, extras.get(1).getType());
    }

    @Test
    void testEscapeCardGranted() {
        when(mockDeck.drawCard()).thenReturn(new EscapeRiskCard(4, "Freiheitskarte", "Du darfst das Gefängnis verlassen"));
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertTrue(player.hasEscapeCard());
        assertEquals(2, extras.size());
        assertEquals(MessageType.PLAYER_OUT_OF_JAIL_CARD, extras.get(1).getType());
    }

    @Test
    void testGoToJailCardWithoutEscape() {
        when(mockDeck.drawCard()).thenReturn(new GoToJailRiskCard(5, "Geh ins Gefängnis", "Du wirst verhaftet"));
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertEquals(jailTile, player.getCurrentTile());
        assertEquals(3, player.getSuspensionRounds());
        assertEquals(1, extras.size());
        assertEquals(MessageType.DRAW_RISK_CARD, extras.get(0).getType());
    }

    @Test
    void testGoToJailCardWithEscapeCard() {
        player.setEscapeCard(true);
        when(mockDeck.drawCard()).thenReturn(new GoToJailRiskCard(6, "Geh ins Gefängnis", "Aber du hast eine Freiheitskarte"));
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, payloadForPlayer(), gameState, extras);

        assertFalse(player.hasEscapeCard());
        assertEquals(1, extras.size());
        assertTrue(extras.get(0).getPayload().toString().contains("Freiheitskarte"));
    }

    @Test
    void testInvalidPlayer() {
        Map<String, Object> badPayload = Map.of("playerId", 999);
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, badPayload, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Spieler ungültig"));
    }

    @Test
    void testExceptionHandling() {
        Map<String, Object> invalid = new HashMap<>(); // no playerId
        List<GameMessage> extras = new ArrayList<>();

        GameMessage result = request.execute(lobbyId, invalid, gameState, extras);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Fehler beim Ziehen"));
    }
}
