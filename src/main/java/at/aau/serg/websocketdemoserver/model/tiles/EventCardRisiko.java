package at.aau.serg.websocketdemoserver.model.tiles;

public class EventCardRisiko extends EventCard {

    public EventCardRisiko(String description, int amount) {
        super("Risikokarte", description, amount);
    }

    @Override
    public String getType(){
        return "risiko";
    }
}
