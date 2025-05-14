package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerDTO;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameHandlerTest {

    private GameHandler gameHandler;
    private GameState mockGameState;

    @BeforeEach
    void setUp() {
        // Mock GameState und Tile
        mockGameState = Mockito.mock(GameState.class);
        GameBoard mockBoard = Mockito.mock(GameBoard.class);
        Tile jailTile = Mockito.mock(Tile.class);

        // Verhalten für getBoard() und getTile(31)
        Mockito.when(mockGameState.getBoard()).thenReturn(mockBoard);
        Mockito.when(mockBoard.getTile(31)).thenReturn(jailTile);

        gameHandler = new GameHandler(mockGameState);
    }

    @Test
    void handle_withNullMessage_returnsError() {
        GameMessage result = gameHandler.handle(null);

        assertEquals(MessageType.ERROR, result.getType());
        assertEquals(-1, result.getLobbyId());
    }

    @Test
    void handle_withUnknownType_returnsError() throws JSONException {
        GameMessage msg = new GameMessage(1, null, null);
        GameMessage result = gameHandler.handle(msg);

        assertEquals(MessageType.ERROR, result.getType());
        assertEquals(1, result.getLobbyId());
    }

    @Test
    void handle_withRequestGameState_returnsGameStateMessage() {
        GameMessage msg = new GameMessage(1, MessageType.REQUEST_GAME_STATE, null);

        GameMessage result = gameHandler.handle(msg);

        assertEquals(MessageType.GAME_STATE, result.getType());
        assertEquals(1, result.getLobbyId());
    }

    @Test
    void handle_withUnsupportedMessageType_returnsError() throws JSONException {
        GameMessage msg = new GameMessage(2, MessageType.ERROR, null); // ERROR nicht im Map

        GameMessage result = gameHandler.handle(msg);

        assertEquals(MessageType.ERROR, result.getType());
        assertEquals(2, result.getLobbyId());
    }

    @Test
    void initGame_setsUpPlayersCorrectly() {
        GameBoard mockBoard = Mockito.mock(GameBoard.class);
        Mockito.when(mockGameState.getBoard()).thenReturn(mockBoard);

        PlayerDTO p1 = new PlayerDTO(1, "Alice");
        PlayerDTO p2 = new PlayerDTO(2, "Bob");

        gameHandler.initGame(List.of(p1, p2));

        Mockito.verify(mockGameState).startGame(Mockito.argThat(players ->
                players.size() == 2 &&
                        players.get(0).getNickname().equals("Alice") &&
                        players.get(1).getNickname().equals("Bob")
        ));
    }

    @Test
    void getExtraMessages_returnsCopy() {
        // simulate PAY_TAX adds an extra message
        GameMessage taxMessage = new GameMessage(3, MessageType.PAY_TAX, new Object());

        GameMessage result = gameHandler.handle(taxMessage);

        List<GameMessage> extras = gameHandler.getExtraMessages();

        assertNotNull(extras);
        assertFalse(extras.contains(result)); // primary message is separate
    }

    @Test
    void getCurrentPlayerId_returnsCorrectId() {
        Player mockPlayer = Mockito.mock(Player.class);
        Mockito.when(mockPlayer.getId()).thenReturn(7);
        Mockito.when(mockGameState.getCurrentPlayer()).thenReturn(mockPlayer);

        String id = gameHandler.getCurrentPlayerId();

        assertEquals("7", id);
    }

    @Test
    void getCurrentPlayerId_returnsNullWhenNoPlayer() {
        Mockito.when(mockGameState.getCurrentPlayer()).thenReturn(null);

        String id = gameHandler.getCurrentPlayerId();

        assertNull(id);
    }
}
