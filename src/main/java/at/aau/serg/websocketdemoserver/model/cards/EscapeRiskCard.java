package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.dto.PlayerOutOfJailCardPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class EscapeRiskCard extends RiskCard {

    public EscapeRiskCard(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public RiskCardEffect getEffect() {
        return RiskCardEffect.ESCAPE_CARD;
    }
}
