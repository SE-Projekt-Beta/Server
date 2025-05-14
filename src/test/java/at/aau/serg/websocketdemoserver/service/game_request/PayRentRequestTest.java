package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PayRentRequestTest {

    private GameState gameState;
    private Player player;
    private Player owner;
    private StreetTile street;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        GameBoard board = gameState.getBoard();

        // Erstelle Spieler
        player = new Player("Thomas", board);
        owner = new Player("Eva", board);

        // Spiel starten
        gameState.startGame(new ArrayList<>(List.of(player, owner)));


        // Erstelle und setze StreetTile mit Besitzer
        street = new StreetTile(5, "Mietstraße", 200, 50, StreetLevel.NORMAL, 100);
        board.getTiles().set(5, street);
        street.setOwner(owner);
    }

    @Test
    void testNoRentIfOwnerIsSelf() {
        street.setOwner(player); // Spieler besitzt Straße selbst

        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", street.getIndex()
        );

        PayRentRequest request = new PayRentRequest();
        List<GameMessage> extraMessages = new ArrayList<>();
        GameMessage result = request.execute(1, payload, gameState, extraMessages);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Keine Miete zu zahlen."));
    }

    @Test
    void testNoRentIfTileIsInvalid() {
        Map<String, Object> payload = Map.of(
                "playerId", player.getId(),
                "tilePos", 0 // kein StreetTile
        );

        PayRentRequest request = new PayRentRequest();
        List<GameMessage> extraMessages = new ArrayList<>();
        GameMessage result = request.execute(1, payload, gameState, extraMessages);

        assertEquals(MessageType.ERROR, result.getType());
        assertTrue(result.getPayload().toString().contains("Kein gültiges Mietfeld."));
    }

}