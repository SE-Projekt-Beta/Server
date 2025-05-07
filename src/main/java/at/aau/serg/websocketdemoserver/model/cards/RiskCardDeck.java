package at.aau.serg.websocketdemoserver.model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiskCardDeck {

    private final List<RiskCard> riskCards;

    public RiskCardDeck() {
        riskCards = new ArrayList<>();
        riskCards.add(new RiskCard(1, "Jail Time", "Go directly to jail!"));
        riskCards.add(new EscapeRiskCard(2, "Freedom Pass", "You are free from jail."));
        riskCards.add(new CashRiskCard(3, "Lottery Win", "You win a lottery!", 150));
        riskCards.add(new CashRiskCard(4, "Tax Payment", "Pay taxes!", -100));
        riskCards.add(new CashRiskCard(5, "Bonus", "You received a bonus!", 80));
    }

    public RiskCard drawRandomRiskCard() {
        Collections.shuffle(riskCards);
        return riskCards.get(0);
    }
}