package at.aau.serg.websocketdemoserver.dkt.tiles;

import at.aau.serg.websocketdemoserver.dkt.Tile;

public class BankEvent extends Tile{
    public BankEvent(int position, String name) {
        super(position, name);
    }

    @Override
    public String getTileType(){
        return "event_bank";
    }
}
