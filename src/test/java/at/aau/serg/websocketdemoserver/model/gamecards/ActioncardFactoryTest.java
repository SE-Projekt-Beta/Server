package at.aau.serg.websocketdemoserver.model.gamecards;

import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.cards.ActionCardFactory;
import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActionCardFactoryTest {

    @Test
    void testDrawCardFromRiskTile() {
        Tile tile = new RiskTile(3);
        Optional<ActionCard> card = ActionCardFactory.drawCard(tile);

        assertTrue(card.isPresent());
        assertTrue(card.get() instanceof RiskCard);
    }

    @Test
    void testDrawCardFromBankTile() {
        Tile tile = new BankTile(9);
        Optional<ActionCard> card = ActionCardFactory.drawCard(tile);

        assertTrue(card.isPresent());
        assertTrue(card.get() instanceof BankCard);
    }

    @Test
    void testDrawCardFromOtherTileReturnsEmpty() {
        Tile tile = new StreetTile(5, "Test Street", 100, 20, StreetLevel.NORMAL, 50);
        Optional<ActionCard> card = ActionCardFactory.drawCard(tile);

        assertTrue(card.isEmpty());
    }

    @Test
    void testPrivateConstructorViaReflection() throws Exception {
        var constructor = ActionCardFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
