package at.aau.serg.websocketdemoserver.dkt;
import com.fasterxml.jackson.annotation.*;
import at.aau.serg.websocketdemoserver.dkt.tiles.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Event.class, name = "event"),
        @JsonSubTypes.Type(value = Free.class, name = "free"),
        @JsonSubTypes.Type(value = GoToJail.class, name = "goToJail"),
        @JsonSubTypes.Type(value = Jail.class, name = "jail"),
        @JsonSubTypes.Type(value = Start.class, name = "start"),
        @JsonSubTypes.Type(value = Station.class, name = "station"),
        @JsonSubTypes.Type(value = Street.class, name = "street"),
        @JsonSubTypes.Type(value = Tax.class, name = "tax"),
})
public class Tile {
    private final int position;
    private final String name;


    public Tile(int position, String name) {
        this.position = position;
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getTileType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

}
