package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.cards.CashRiskCard;
import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.RiskCard;

import java.util.List;
import java.util.Random;

public class EventCardService {

    private final List<RiskCard> eventCardsRisiko = List.of(
            new RiskCard("Gehe 3 Felder zurück", -3),
            new RiskCard("Gehe 2 Felder vor", 2),
            new RiskCard("Gehe 4 Felder zurück", -4),
            new RiskCard("Gehe 4 Felder vor", 4)
    );

    private final List<BankCard> eventCardsBank = List.of(
            new BankCard("Für Unfallversicherung bezahlst du 200,-", -200),
            new BankCard("Für eine Autoreparatur bezahlst du 140,-", -140),
            new BankCard("Für die Auswertung einer Erfindung erhälst du 140,- aus öffentlichen Mitteln", 140),
            new BankCard("Die Bank zahlt dir an Dividenden 60,-", 60)
    );

    private final Random rand = new Random();

    public CashRiskCard drawCard(String type){
        if ("risiko".equalsIgnoreCase(type)) {
            return drawRisikoCard();
        } else if ("bank".equalsIgnoreCase(type)) {
            return drawBankCard();
        } else {
            throw new IllegalArgumentException("Unbekannter Event-Typ: " + type);
        }
    }

    public BankCard drawBankCard() {
        return eventCardsBank.get(rand.nextInt(eventCardsBank.size()));
    }

    public RiskCard drawRisikoCard() {
        return eventCardsRisiko.get(rand.nextInt(eventCardsRisiko.size()));
    }
}