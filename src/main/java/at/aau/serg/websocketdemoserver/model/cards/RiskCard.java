package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class RiskCard extends ActionCard {

    public RiskCard(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public void execute(Player player) {
        // Send action to server (pseudo-implementation)
        System.out.println("Executing RiskCard for player " + player.getNickname() + ": " + getDescription());
    }
}
