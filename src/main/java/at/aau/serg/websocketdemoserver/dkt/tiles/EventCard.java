package at.aau.serg.websocketdemoserver.dkt.tiles;

import at.aau.serg.websocketdemoserver.dkt.Tile;


public abstract class EventCard {

    private final String title;
    private final String description;
    private int amount;

    public EventCard(String title, String description, int amount){
        this.title = title;
        this.description = description;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public abstract String getType();
}
