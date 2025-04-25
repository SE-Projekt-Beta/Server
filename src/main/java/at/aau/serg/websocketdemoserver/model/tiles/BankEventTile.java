package at.aau.serg.websocketdemoserver.model.tiles;

import at.aau.serg.websocketdemoserver.model.Tile;

public class BankEventTile extends Tile {
    public BankEventTile(int position, String name) {
        super(position, name);
    }

    @Override
    public String getTileType(){
        return "event_bank";
    }
}
