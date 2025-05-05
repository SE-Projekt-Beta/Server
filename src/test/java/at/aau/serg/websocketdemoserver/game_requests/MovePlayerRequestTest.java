package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.MovePlayerPayload;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.MovePlayerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MovePlayerRequestTest {

    private MovePlayerRequest request;
    private GameState gameState;
    private GameMessage message;
    private Player player;
    private Tile tile;

    @BeforeEach
    void setUp() {
        request = new MovePlayerRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        player = mock(Player.class);
        tile = mock(Tile.class);
    }

    @Test
    void testPlayerMovesCorrectly_createsCorrectPayload() {
        // Arrange
        when(gameState.getCurrentPlayer()).thenReturn(player);
        when(player.getId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(player);

        when(message.parsePayload(Integer.class)).thenReturn(4); // dice = 4
        when(player.calculateNewPosition(4)).thenReturn(7);

        GameBoard mockBoard = mock(GameBoard.class);
        when(player.getCurrentTile()).thenReturn(tile);
        when(tile.getLabel()).thenReturn("Hauptstraße");
        when(tile.getType()).thenReturn(TileType.STREET);

        try (MockedStatic<GameBoard> staticMock = mockStatic(GameBoard.class)) {
            staticMock.when(GameBoard::get).thenReturn(mockBoard);

            // Act
            GameMessage result = request.execute(gameState, message);

            // Assert
            verify(player).moveToTile(7, mockBoard);
            assertEquals(MessageType.PLAYER_MOVED, result.getType());

            MovePlayerPayload payload = (MovePlayerPayload) result.getPayload();
            assertEquals(1, payload.getPlayerId());
            assertEquals(4, payload.getDice());
            assertEquals(7, payload.getPos());
            assertEquals("Hauptstraße", payload.getTileName());
            assertEquals("STREET", payload.getTileType());
        }
    }
}
