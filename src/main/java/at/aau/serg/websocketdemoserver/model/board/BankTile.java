package at.aau.serg.websocketdemoserver.model.board;

import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.cards.ActionCardFactory;

import java.util.Optional;

public class BankTile extends Tile {

    public BankTile(int index) {
        super(index);
        setLabel("Bankfeld");
    }

    public Optional<ActionCard> drawBankCard() {
        return ActionCardFactory.drawCard(this);
    }

    @Override
    public TileType getType() {
        return TileType.BANK;
    }
}