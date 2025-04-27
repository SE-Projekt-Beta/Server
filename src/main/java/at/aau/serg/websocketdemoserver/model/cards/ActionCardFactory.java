package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.model.board.BankTile;
import at.aau.serg.websocketdemoserver.model.board.RiskTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;

public class ActionCardFactory {

    private static final RiskCardDeck riskDeck = new RiskCardDeck();
    private static final BankCardDeck bankDeck = new BankCardDeck();

    private ActionCardFactory() {
        // prevent instantiation
    }

    public static ActionCard drawCard(Tile tile) {
        if (tile instanceof RiskTile) {
            return riskDeck.drawRandomRiskCard();
        }
        if (tile instanceof BankTile) {
            return bankDeck.drawRandomBankCard();
        }
        return null;
    }
}
