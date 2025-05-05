package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PropertyListUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyListUpdateRequestTest {

    private PropertyListUpdateRequest request;
    private GameState gameState;
    private GameMessage message;
    private Player player;

    @BeforeEach
    void setUp() {
        request = new PropertyListUpdateRequest();
        gameState = mock(GameState.class);
        message = mock(GameMessage.class);
        player = mock(Player.class);
    }

    @Test
    void testPlayerNotFound_returnsError() {
        when(message.parsePayload(Integer.class)).thenReturn(1);
        when(gameState.getPlayer(1)).thenReturn(null);

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testPlayerFound_returnsPropertyListUpdate() {
        when(message.parsePayload(Integer.class)).thenReturn(2);
        when(gameState.getPlayer(2)).thenReturn(player);

        StreetTile tile1 = mock(StreetTile.class);
        StreetTile tile2 = mock(StreetTile.class);

        when(tile1.getIndex()).thenReturn(5);
        when(tile1.getName()).thenReturn("Bahnhofstraße");
        when(tile1.getPrice()).thenReturn(200);
        when(tile1.getBaseRent()).thenReturn(50);
        when(tile1.getOwnerId()).thenReturn(2);
        when(tile1.getOwnerName()).thenReturn("Max");
        when(tile1.getHouseCount()).thenReturn(2);
        when(tile1.getHotelCount()).thenReturn(0);

        when(tile2.getIndex()).thenReturn(9);
        when(tile2.getName()).thenReturn("Ringstraße");
        when(tile2.getPrice()).thenReturn(350);
        when(tile2.getBaseRent()).thenReturn(90);
        when(tile2.getOwnerId()).thenReturn(2);
        when(tile2.getOwnerName()).thenReturn("Max");
        when(tile2.getHouseCount()).thenReturn(0);
        when(tile2.getHotelCount()).thenReturn(1);

        when(gameState.getOwnedProperties(player)).thenReturn(List.of(tile1, tile2));

        GameMessage result = request.execute(gameState, message);
        assertEquals(MessageType.PROPERTY_LIST_UPDATE, result.getType());

        PropertyListUpdatePayload payload = (PropertyListUpdatePayload) result.getPayload();
        assertEquals(2, payload.getPlayerId());
        assertEquals(2, payload.getOwnedStreets().size());

        PropertyEntry entry1 = payload.getOwnedStreets().get(0);
        assertEquals(5, entry1.getTileIndex());
        assertEquals("Bahnhofstraße", entry1.getTileName());
        assertEquals(200, entry1.getPrice());
        assertEquals(50, entry1.getBaseRent());
        assertEquals(2, entry1.getOwnerId());
        assertEquals("Max", entry1.getOwnerName());
        assertEquals(2, entry1.getHouseCount());
        assertEquals(0, entry1.getHotelCount());

        PropertyEntry entry2 = payload.getOwnedStreets().get(1);
        assertEquals(9, entry2.getTileIndex());
        assertEquals("Ringstraße", entry2.getTileName());
        assertEquals(350, entry2.getPrice());
        assertEquals(90, entry2.getBaseRent());
        assertEquals(2, entry2.getOwnerId());
        assertEquals("Max", entry2.getOwnerName());
        assertEquals(0, entry2.getHouseCount());
        assertEquals(1, entry2.getHotelCount());
    }
}
