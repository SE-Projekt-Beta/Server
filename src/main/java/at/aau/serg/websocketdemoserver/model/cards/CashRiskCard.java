package at.aau.serg.websocketdemoserver.model.cards;


public class CashRiskCard extends RiskCard {
    private final int amount;

    public CashRiskCard(int id, String title, String description, int amount) {
        super(id, title, description);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public RiskCardEffect getEffect() {
        return RiskCardEffect.CASH;
    }
}
