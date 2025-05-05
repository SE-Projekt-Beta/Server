package at.aau.serg.websocketdemoserver.model.board;

import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.cards.ActionCardFactory;

import java.util.Optional;

public class RiskTile extends Tile {

    public RiskTile(int index) {
        super(index);
        setLabel("Risiko");
    }

    public Optional<ActionCard> drawRiskCard() {
        return ActionCardFactory.drawCard(this);
    }

    @Override
    public TileType getType() {
        return TileType.RISK;
    }
}
