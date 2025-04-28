package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class CashBankCard extends BankCard {

    private final int amount;

    public CashBankCard(int id, String title, String description, int amount) {
        super(id, title, description);
        this.amount = amount;
    }

    @Override
    public void execute(Player player) {
        player.setCash(player.getCash() + amount);
        System.out.println("CashBankCard applied: " + (amount >= 0 ? "+" : "") + amount + " to " + player.getNickname());
    }
}
