package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.GameEventPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class BankCard extends ActionCard {

    public BankCard(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public GameMessage execute(Player player) {
        GameEventPayload payload = new GameEventPayload(getTitle(), getDescription());
        return new GameMessage(MessageType.EVENT_CARD_DRAWN, payload);
    }
}
