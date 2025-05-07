package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public abstract class ActionCard {

    private final int id;
    private final String title;
    private final String description;

    public ActionCard(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Execute the card's action for a given player.
     *
     * @param player Player who draws the card.
     */
    public abstract void execute(Player player);
}
