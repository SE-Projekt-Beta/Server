package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MessageFactoryTest {

    @Test
    void testPlayerMoved() {
        GameMessage msg = MessageFactory.playerMoved(1, 5, 2, "Hauptstraße", "StreetTile");
        assertEquals(MessageType.PLAYER_MOVED, msg.getType());
        assertNotNull(msg.getPayload());
    }

    @Test
    void testCurrentPlayer() {
        GameMessage msg = MessageFactory.currentPlayer(1);
        assertEquals(MessageType.CURRENT_PLAYER, msg.getType());

        Map<?, ?> payload = (Map<?, ?>) msg.getPayload();
        assertEquals("1", payload.get("playerId"));
    }

    @Test
    void testPropertyBought() {
        GameMessage msg = MessageFactory.propertyBought(2, 10, "Bahnhofstraße");
        assertEquals(MessageType.PROPERTY_BOUGHT, msg.getType());
        assertNotNull(msg.getPayload());
    }

    @Test
    void testCanBuyProperty() {
        GameMessage msg = MessageFactory.canBuyProperty(3, 15, "Marktplatz", 300);
        assertEquals(MessageType.CAN_BUY_PROPERTY, msg.getType());

        Map<?, ?> payload = (Map<?, ?>) msg.getPayload();
        assertEquals(3, payload.get("playerId"));
        assertEquals(15, payload.get("tilePos"));
        assertEquals("Marktplatz", payload.get("tileName"));
        assertEquals(300, payload.get("price"));
    }

    @Test
    void testMustPayRent() {
        GameMessage msg = MessageFactory.mustPayRent(4, 2, 18, "Ringstraße", 50);
        assertEquals(MessageType.MUST_PAY_RENT, msg.getType());

        Map<?, ?> payload = (Map<?, ?>) msg.getPayload();
        assertEquals(4, payload.get("playerId"));
        assertEquals(2, payload.get("ownerId"));
        assertEquals(18, payload.get("tilePos"));
        assertEquals("Ringstraße", payload.get("tileName"));
        assertEquals(50, payload.get("amount"));
    }

    @Test
    void testDrawEventCardBank() {
        GameMessage msg = MessageFactory.drawEventCard("bank", "Banküberfall", "Du wirst ausgeraubt!");
        assertEquals(MessageType.DRAW_EVENT_BANK_CARD, msg.getType());

        Map<?, ?> payload = (Map<?, ?>) msg.getPayload();
        assertEquals("Banküberfall", payload.get("title"));
        assertEquals("Du wirst ausgeraubt!", payload.get("description"));
    }

    @Test
    void testDrawEventCardRisiko() {
        GameMessage msg = MessageFactory.drawEventCard("risiko", "Unfall", "Auto kaputt.");
        assertEquals(MessageType.DRAW_EVENT_RISIKO_CARD, msg.getType());

        Map<?, ?> payload = (Map<?, ?>) msg.getPayload();
        assertEquals("Unfall", payload.get("title"));
        assertEquals("Auto kaputt.", payload.get("description"));
    }

    @Test
    void testDrawEventCardInvalid() {
        GameMessage msg = MessageFactory.drawEventCard("unknown", "Nichts", "Keine Wirkung.");
        assertEquals(MessageType.ERROR, msg.getType());
    }

    @Test
    void testGoToJail() {
        GameMessage msg = MessageFactory.goToJail(5);
        assertEquals(MessageType.GO_TO_JAIL, msg.getType());

        Map<?, ?> payload = (Map<?, ?>) msg.getPayload();
        assertEquals(5, payload.get("playerId"));
        assertEquals(10, payload.get("tilePos"));
        assertEquals("Gefängnis", payload.get("tileName"));
    }

    @Test
    void testSkippedTurn() {
        GameMessage msg = MessageFactory.skippedTurn(6, 12, "Parkplatz");
        assertEquals(MessageType.SKIPPED, msg.getType());

        Map<?, ?> payload = (Map<?, ?>) msg.getPayload();
        assertEquals(6, payload.get("playerId"));
        assertEquals(12, payload.get("tilePos"));
        assertEquals("Parkplatz", payload.get("tileName"));
    }

    @Test
    void testError() {
        GameMessage msg = MessageFactory.error("Testfehler");
        assertEquals(MessageType.ERROR, msg.getType());
        assertEquals("Testfehler", msg.getPayload());
    }
}