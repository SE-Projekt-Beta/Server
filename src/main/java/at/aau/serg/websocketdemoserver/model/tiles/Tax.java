package at.aau.serg.websocketdemoserver.model.tiles;

import at.aau.serg.websocketdemoserver.model.Tile;

public class Tax extends Tile {

    int amount = 0;

    public Tax(int position, String name, int amount) {
        super(position, name);
        this.amount = amount;
    }

}
