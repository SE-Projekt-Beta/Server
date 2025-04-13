package at.aau.serg.websocketdemoserver.dkt.tiles;

public class EventCardBank extends EventCard {

    public EventCardBank(String description, int amount) {
        super("Bankkarte", description, amount);
    }

    @Override
    public String getType(){
        return "bank";
    }
}
