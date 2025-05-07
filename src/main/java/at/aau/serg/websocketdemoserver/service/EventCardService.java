package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.cards.*;

public class EventCardService {

    private final BankCardDeck bankDeck;
    private final RiskCardDeck riskDeck;

    public EventCardService(BankCardDeck bankDeck, RiskCardDeck riskDeck) {
        this.bankDeck = bankDeck;
        this.riskDeck = riskDeck;
    }

    public BankCard drawBankCard() {
        return bankDeck.drawCard();
    }

    public RiskCard drawRiskCard() {
        return riskDeck.drawCard();
    }
}
