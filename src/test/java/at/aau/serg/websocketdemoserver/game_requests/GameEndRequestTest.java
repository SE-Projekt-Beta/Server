package at.aau.serg.websocketdemoserver.game_requests;

import at.aau.serg.websocketdemoserver.dto.GameEndedPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTileFactory;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameEndRequestTest {

    @BeforeEach
    void resetCounter() {
        Player.resetIdCounter(); // Damit IDs konsistent sind
    }

    @Test
    void testExecute_returnsCorrectRanking() {
        GameBoard board = new GameBoard();
        GameState state = new GameState(board);

        Player p1 = new Player("Alice", board);
        Player p2 = new Player("Bob", board);
        Player p3 = new Player("Clara", board);

        p1.setCash(1000);
        p2.setCash(2000);
        p3.setCash(1500);

        // Besitz hinzufügen über GameBoard direkt (z. B. bei Position 2 & 5)
        board.getTiles().set(2, StreetTileFactory.createStreetTile(2));
        board.getTiles().set(5, StreetTileFactory.createStreetTile(5));
        p1.purchaseStreet(2); // Preis wird vom Cash abgezogen
        p3.purchaseStreet(5);

        state.addPlayer(p1);
        state.addPlayer(p2);
        state.addPlayer(p3);

        GameMessage result = new GameEndRequest().execute(state, new GameMessage(MessageType.END_GAME, null));

        assertEquals(MessageType.END_GAME, result.getType());

        GameEndedPayload payload = result.parsePayload(GameEndedPayload.class);
        List<GameEndedPayload.PlayerRankingEntry> ranking = payload.getRanking();

        assertEquals(3, ranking.size());
        assertEquals("Bob", ranking.get(0).getNickname());
        assertEquals("Clara", ranking.get(1).getNickname());
        assertEquals("Alice", ranking.get(2).getNickname());

        // Rangprüfung (sollte mit höchstem Vermögen starten)
        assertEquals(1, ranking.get(0).getRank());
        assertTrue(ranking.get(0).getWealth() >= ranking.get(1).getWealth());
    }
}
