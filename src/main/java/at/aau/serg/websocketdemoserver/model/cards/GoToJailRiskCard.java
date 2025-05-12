package at.aau.serg.websocketdemoserver.model.cards;

public class GoToJailRiskCard extends RiskCard {

    public GoToJailRiskCard(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public RiskCardEffect getEffect() {
        return RiskCardEffect.GOTO_JAIL;
    }
}
