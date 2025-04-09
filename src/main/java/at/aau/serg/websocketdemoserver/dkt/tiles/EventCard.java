package at.aau.serg.websocketdemoserver.dkt.tiles;

import lombok.Getter;

public abstract class EventCard {
    @Getter
    private String title;
    @Getter
    private String description;
    @Getter
    private int amount;

    public EventCard(String title, String description, int amount){
        this.title = title;
        this.description = description;
        this.amount = amount;
    }
}
