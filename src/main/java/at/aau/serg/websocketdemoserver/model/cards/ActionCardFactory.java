package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.model.board.BankTile;
import at.aau.serg.websocketdemoserver.model.board.RiskTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;

import java.util.Optional;

public class ActionCardFactory {

    // ActionCardFactory.java
    private static final RiskCardDeck riskDeck = RiskCardDeck.get();
    private static final BankCardDeck bankDeck = BankCardDeck.get();

    public static Optional<ActionCard> drawCard(Tile tile) {
        if (tile instanceof RiskTile) {
            return Optional.ofNullable(riskDeck.drawCard());
        }
        if (tile instanceof BankTile) {
            return Optional.ofNullable(bankDeck.drawCard());
        }
        return Optional.empty();
    }

}