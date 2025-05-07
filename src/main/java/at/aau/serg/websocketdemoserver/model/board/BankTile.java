package at.aau.serg.websocketdemoserver.model.board;

import at.aau.serg.websocketdemoserver.model.cards.ActionCard;
import at.aau.serg.websocketdemoserver.model.cards.ActionCardFactory;

public class BankTile extends Tile {

    public BankTile(int index) {
        super(index);
    }

    public ActionCard drawBankCard() {
        return ActionCardFactory.drawCard(this);
    }
}