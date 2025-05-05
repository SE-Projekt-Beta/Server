package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;
import at.aau.serg.websocketdemoserver.model.cards.RiskCardDeck;

public class EventCardService {

    private static final EventCardService instance = new EventCardService();

    private EventCardService() {}

    public static EventCardService get() {
        return instance;
    }

    public BankCard drawBankCard() {
        return BankCardDeck.get().drawCard();
    }

    public RiskCard drawRiskCard() {
        return RiskCardDeck.get().drawCard();
    }
}
