package at.aau.serg.websocketdemoserver.model.cards;

public class BankCard {
    private final int id;
    private final String title;
    private final String description;
    private final int amount;

    public BankCard(int id, String title, String description, int amount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }
}
