package at.aau.serg.websocketdemoserver.dto;

public class EventCardPayload {
    private String title;
    private String description;
    private int amount;
    private String eventType; // "bank" oder "risiko"

    public EventCardPayload() {
    }

    public EventCardPayload(String title, String description, int amount, String eventType) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.eventType = eventType;
    }

    // Getter und Setter
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
