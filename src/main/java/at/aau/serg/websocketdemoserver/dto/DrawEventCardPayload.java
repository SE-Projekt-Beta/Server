package at.aau.serg.websocketdemoserver.dto;

/**
 * Payload für das Ziehen einer Ereigniskarte (Bank- oder Risikoereignis).
 */
public class DrawEventCardPayload {

    private String eventType;
    private String title;
    private String description;

    public DrawEventCardPayload() {
        // Standard-Konstruktor für Deserialisierung
    }

    public DrawEventCardPayload(String eventType, String title, String description) {
        this.eventType = eventType;
        this.title = title;
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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