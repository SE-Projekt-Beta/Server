package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class EscapeRiskCard extends RiskCard {

    public EscapeRiskCard(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public void execute(Player player) {
        player.setEscapeCard(true);
        System.out.println("EscapeRiskCard granted to " + player.getNickname());
    }
}
