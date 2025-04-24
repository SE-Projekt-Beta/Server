package at.aau.serg.websocketdemoserver.model.tiles;

import at.aau.serg.websocketdemoserver.model.Tile;

public class GoToJail extends Tile {
    public GoToJail(int position, String name) {
        super(position, name);
    }

    @Override
    public String getTileType(){
        return "go_to_jail";
    }
}
