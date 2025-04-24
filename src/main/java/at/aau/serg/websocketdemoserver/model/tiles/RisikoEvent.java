package at.aau.serg.websocketdemoserver.model.tiles;

import at.aau.serg.websocketdemoserver.model.Tile;

public class RisikoEvent extends Tile {
    // What does this do?

    public RisikoEvent(int position, String name) {
        super(position, name);
    }

    @Override
    public String getTileType(){
        return "event_risiko";
    }

}
