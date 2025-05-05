package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.GoToJailPayload;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.JailTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.GoToJailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoToJailRequestTest {

    private GoToJailRequest request;
    private GameState gameState;
    private Player player;
    private JailTile jailTile;

    @BeforeEach
    void setUp() {
        request = new GoToJailRequest();
        gameState = mock(GameState.class);
        player = mock(Player.class);
        jailTile = mock(JailTile.class);
    }

    @Test
    void testSuccessfulGoToJail() {
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getId()).thenReturn(7);
        when(gameState.getPlayer(7)).thenReturn(player);
        when(jailTile.getIndex()).thenReturn(10);
        when(player.getSuspensionRounds()).thenReturn(2);

        GameBoard mockBoard = mock(GameBoard.class);
        when(mockBoard.getTiles()).thenReturn(List.of(jailTile));

        try (MockedStatic<GameBoard> boardMock = mockStatic(GameBoard.class)) {
            boardMock.when(GameBoard::get).thenReturn(mockBoard);

            GameMessage result = request.execute(gameState, null);

            verify(player).moveToTile(10, mockBoard);
            verify(player).suspendForRounds(2);

            assertEquals(MessageType.GO_TO_JAIL, result.getType());
            GoToJailPayload payload = (GoToJailPayload) result.getPayload();
            assertEquals(7, payload.getPlayerId());
            assertEquals(10, payload.getJailPosition());
            assertEquals(2, payload.getRoundsInJail());
            assertEquals("Du wurdest ins Gefängnis geschickt.", payload.getReason());
        }
    }

    @Test
    void testNoJailTileFound_throwsException() {
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getId()).thenReturn(99);
        when(gameState.getPlayer(99)).thenReturn(player);

        GameBoard mockBoard = mock(GameBoard.class);
        when(mockBoard.getTiles()).thenReturn(List.of(mock(Tile.class))); // Kein JailTile

        try (MockedStatic<GameBoard> boardMock = mockStatic(GameBoard.class)) {
            boardMock.when(GameBoard::get).thenReturn(mockBoard);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                request.execute(gameState, null);
            });

            assertEquals("Kein Gefängnisfeld gefunden.", exception.getMessage());
        }
    }
}
