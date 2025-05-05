package at.aau.serg.websocketdemoserver.model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankCardDeck {

    private static BankCardDeck instance;

    private final List<BankCard> bankCards;

    private BankCardDeck() {
        bankCards = new ArrayList<>();
        bankCards.add(new BankCard(1, "Parkstrafe", "Du musst eine Parkstrafe zahlen."));
        bankCards.add(new CashBankCard(2, "Bankirrtum", "Ein Bankirrtum zu deinen Gunsten.", 200));
        bankCards.add(new CashBankCard(3, "Versicherungsprämie", "Du erhältst eine Versicherungsprämie.", 100));
        bankCards.add(new CashBankCard(4, "Autoreparatur", "Du musst eine Autoreparatur bezahlen.", -150));
        bankCards.add(new CashBankCard(5, "Erbschaft", "Du erhältst eine Erbschaft.", 300));
        bankCards.add(new CashBankCard(6, "Spendenzahlung", "Du spendest für einen guten Zweck.", -100));
    }

    public static BankCardDeck get() {
        if (instance == null) {
            instance = new BankCardDeck();
        }
        return instance;
    }

    public BankCard drawCard() {
        Collections.shuffle(bankCards);
        return bankCards.get(0);
    }
}
