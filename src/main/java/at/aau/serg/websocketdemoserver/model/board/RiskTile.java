package at.aau.serg.websocketdemoserver.model.board;

import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.cards.ActionCardFactory;

public class RiskTile extends Tile {

    public RiskTile(int index) {
        super(index);
    }

    public ActionCard drawRiskCard() {
        return ActionCardFactory.drawCard(this);
    }
}