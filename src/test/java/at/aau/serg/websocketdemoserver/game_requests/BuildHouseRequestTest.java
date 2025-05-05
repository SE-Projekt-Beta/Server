package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.BuildPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.BuildHouseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BuildHouseRequestTest {

    private BuildHouseRequest request;
    private GameState gameState;
    private GameMessage message;
    private BuildPropertyPayload payload;
    private Player player;
    private StreetTile street;

    @BeforeEach
    void setUp() {
        request = new BuildHouseRequest();
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
        Tile nonStreetTile = mock(Tile.class);
        when(player.getCurrentTile()).thenReturn(nonStreetTile);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotOwner_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(3);
        when(gameState.getPlayer(3)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(mock(Player.class)); // Fremder Besitzer

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testNotEnoughCash_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(4);
        when(gameState.getPlayer(4)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(player);
        when(player.getCash()).thenReturn(100);
        when(street.getHouseCost()).thenReturn(200);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testBuildHouseFails_returnsError() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(5);
        when(gameState.getPlayer(5)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(player);
        when(player.getCash()).thenReturn(300);
        when(street.getHouseCost()).thenReturn(200);
        when(street.buildHouse()).thenReturn(false);

        GameMessage result = request.execute(gameState, message);

        assertEquals(MessageType.ERROR, result.getType());
    }

    @Test
    void testSuccessfulHouseBuild_returnsPropertyListUpdate() {
        when(message.parsePayload(BuildPropertyPayload.class)).thenReturn(payload);
        when(payload.getPlayerId()).thenReturn(6);
        when(gameState.getPlayer(6)).thenReturn(player);
        when(player.getCurrentTile()).thenReturn(street);
        when(street.getOwner()).thenReturn(player);
        when(player.getCash()).thenReturn(500);
        when(street.getHouseCost()).thenReturn(200);
        when(street.buildHouse()).thenReturn(true);
        when(gameState.getOwnedProperties(player)).thenReturn(Collections.emptyList());

        GameMessage result = request.execute(gameState, message);

        verify(player).setCash(300); // 500 - 200
        assertEquals(MessageType.PROPERTY_LIST_UPDATE, result.getType());
        assertEquals(Collections.emptyList(), result.getPayload());
    }
}
