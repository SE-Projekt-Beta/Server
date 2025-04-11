package at.aau.serg.websocketdemoserver.model.tiles;


public abstract class EventCard {
    private String title;
    private String description;
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
}
