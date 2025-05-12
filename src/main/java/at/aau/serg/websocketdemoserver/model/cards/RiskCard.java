package at.aau.serg.websocketdemoserver.model.cards;

public abstract class RiskCard {
    private final int id;
    private final String title;
    private final String description;

    public RiskCard(int id, String title, String description) {
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

    // Effekt wird im Request verarbeitet
    public abstract RiskCardEffect getEffect();
}
