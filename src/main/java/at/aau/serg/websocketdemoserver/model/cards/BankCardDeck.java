package at.aau.serg.websocketdemoserver.model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankCardDeck {

    private final List<BankCard> bankCards;

    public BankCardDeck() {
        bankCards = new ArrayList<>();
        bankCards.add(new BankCard(1, "Parking Fine", "You must pay a parking fine."));
        bankCards.add(new CashBankCard(2, "Bank Error", "Bank error in your favor.", 200));
        bankCards.add(new CashBankCard(3, "Insurance Payout", "Insurance payout received.", 100));
        bankCards.add(new CashBankCard(4, "Car Repair", "Car repair bill to pay.", -150));
    }

    public BankCard drawRandomBankCard() {
        Collections.shuffle(bankCards);
        return bankCards.get(0);
    }
}
