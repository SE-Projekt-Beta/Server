package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.BuildPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.BuildHotelRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildHotelRequestTest {

    private BuildHotelRequest request;
    private GameState gameState;
    private GameMessage message;
    private BuildPropertyPayload payload;
    private Player player;
    private StreetTile street;

    @BeforeEach
    void setUp() {
        request = new BuildHotelRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        payload = mock(BuildPropertyPayload.class);
        player = mock(Player.class);
        street = mock(StreetTile.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testTileNotStreet_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(2);
        when(gameState.getPlayer(2)).thenReturn(player);
        Tile tile = mock(Tile.class);
        when(player.getCurrentTile()).thenReturn(tile); // Not StreetTile

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotOwner_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(3);
        when(gameState.getPlayer(3)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(mock(Player.class)); // Not equal to `player`

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotEnoughHouses_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(4);
        when(gameState.getPlayer(4)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(player);
        when(street.getHouseCount()).thenReturn(3); // Less than 4

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotEnoughCash_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(5);
        when(gameState.getPlayer(5)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(player);
        when(street.getHouseCount()).thenReturn(4);
        when(player.getCash()).thenReturn(200);
        when(street.getHotelCost()).thenReturn(300);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testBuildHotelFails_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(6);
        when(gameState.getPlayer(6)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(player);
        when(street.getHouseCount()).thenReturn(4);
        when(player.getCash()).thenReturn(400);
        when(street.getHotelCost()).thenReturn(300);
        when(street.buildHotel()).thenReturn(false); // Build fails

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testSuccessfulHotelBuild_returnsPropertyListUpdate() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(7);
        when(gameState.getPlayer(7)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(player);
        when(street.getHouseCount()).thenReturn(4);
        when(player.getCash()).thenReturn(500);
        when(street.getHotelCost()).thenReturn(300);
        when(street.buildHotel()).thenReturn(true);
        when(gameState.getOwnedProperties(player)).thenReturn(Collections.emptyList());

        GameMessage result = request.execute(gameState, message);

        verify(player).setCash(200); // 500 - 300
        assertEquals(MessageType.PROPERTY_LIST_UPDATE, result.getType());
        assertEquals(Collections.emptyList(), result.getPayload());
    }
}

