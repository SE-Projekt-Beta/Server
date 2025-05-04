package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.TileFactory;

import java.util.Collections;
import java.util.List;

public class GameBoard {

    private static GameBoard instance;

    private final List<Tile> tiles;

    private GameBoard() {
        this.tiles = TileFactory.createTiles();
    }

    public static GameBoard get() {
        if (instance == null) {
            instance = new GameBoard();
        }
        return instance;
    }

    public List<Tile> getTiles() {
        return Collections.unmodifiableList(tiles);
    }

    public Tile getTile(int index) {
        return tiles.stream()
                .filter(tile -> tile.getIndex() == index)
                .findFirst()
                .orElseThrow(() -> new IndexOutOfBoundsException("Ungültiger Tile-Index: " + index));
    }

    public int size() {
        return tiles.size();
    }
}
