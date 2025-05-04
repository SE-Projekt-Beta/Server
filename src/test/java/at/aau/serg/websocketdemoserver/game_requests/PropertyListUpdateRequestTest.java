package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PropertyListUpdatePayload;
import at.aau.serg.websocketdemoserver.dto.PropertyEntry;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.StreetTileFactory;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.PropertyListUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PropertyListUpdateRequestTest {

    private GameState gameState;
    private PropertyListUpdateRequest request;

    @BeforeEach
    void setup() {
        gameState = new GameState(new GameBoard());
        request = new PropertyListUpdateRequest();
    }


    @Test
    void testNoStreetTilesReturnsEmptyList() {
        // Überschreibe Board mit Nicht-StreetTiles
        gameState.getBoard().getTiles().clear(); // leeres Spielfeld

        GameMessage result = request.execute(gameState, new GameMessage(MessageType.PROPERTY_LIST_UPDATE, null));
        PropertyListUpdatePayload payload = result.parsePayload(PropertyListUpdatePayload.class);

        assertNotNull(payload);
        assertNotNull(payload.getEntries());
        assertEquals(0, payload.getEntries().size());
    }
}
