package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class BankCard extends ActionCard {

    public BankCard(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public void execute(Player player) {
        System.out.println("BankCard drawn: " + getDescription());
    }
}