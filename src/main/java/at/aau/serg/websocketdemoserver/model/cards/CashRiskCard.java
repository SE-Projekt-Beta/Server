package at.aau.serg.websocketdemoserver.model.cards;


import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class CashRiskCard extends RiskCard {

    private final int cashAmount;

    public CashRiskCard(int id, String title, String description, int cashAmount) {
        super(id, title, description);
        this.cashAmount = cashAmount;
    }

    @Override
    public void execute(Player player) {
        player.setCash(player.getCash() + cashAmount);
        System.out.println("CashRiskCard applied: " + (cashAmount >= 0 ? "+" : "") + cashAmount + " to " + player.getNickname());
    }
}