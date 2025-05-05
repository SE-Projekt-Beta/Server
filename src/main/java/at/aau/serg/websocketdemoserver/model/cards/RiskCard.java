package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.dto.EventCardDrawnPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class RiskCard extends ActionCard {

    public RiskCard(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public GameMessage execute(Player player) {
        EventCardDrawnPayload payload = new EventCardDrawnPayload(getTitle(), getDescription());
        return new GameMessage(MessageType.EVENT_CARD_DRAWN, payload);
    }
}
