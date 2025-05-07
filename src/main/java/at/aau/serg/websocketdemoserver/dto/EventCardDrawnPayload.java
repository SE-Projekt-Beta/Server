package at.aau.serg.websocketdemoserver.dto;

public class EventCardDrawnPayload {

    private String title;
    private String description;

    public EventCardDrawnPayload() {}

    public EventCardDrawnPayload(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
