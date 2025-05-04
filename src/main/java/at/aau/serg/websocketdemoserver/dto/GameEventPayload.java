package at.aau.serg.websocketdemoserver.dto;

public class GameEventPayload {
    private String title;
    private String description;

    public GameEventPayload() {
    }

    public GameEventPayload(String title, String description) {
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

