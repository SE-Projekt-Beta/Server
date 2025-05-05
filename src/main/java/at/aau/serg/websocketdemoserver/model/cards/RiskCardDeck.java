package at.aau.serg.websocketdemoserver.model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiskCardDeck {

    private static RiskCardDeck instance;

    private final List<RiskCard> riskCards;

    private RiskCardDeck() {
        riskCards = new ArrayList<>();
        riskCards.add(new RiskCard(1, "Jail Time", "Gehe direkt ins Gefängnis!"));
        riskCards.add(new EscapeRiskCard(2, "Freedom Pass", "Du bist frei und kannst das Gefängnis verlassen."));
        riskCards.add(new CashRiskCard(3, "Lottogewinn", "Du gewinnst im Lotto!", 150));
        riskCards.add(new CashRiskCard(4, "Steuernachzahlung", "Du musst Steuern zahlen!", -100));
        riskCards.add(new CashRiskCard(5, "Bonus", "Du erhältst einen Bonus von deinem Arbeitgeber.", 80));
    }

    public static RiskCardDeck get() {
        if (instance == null) {
            instance = new RiskCardDeck();
        }
        return instance;
    }

    public RiskCard drawCard() {
        Collections.shuffle(riskCards);
        return riskCards.get(0);
    }
}
