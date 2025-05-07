package at.aau.serg.websocketdemoserver.model.gamestate;


import java.util.List;
import java.util.Optional;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.*;

public class GameBoard {
    private final List<Tile> tiles;

    public GameBoard() {
        this.tiles = TileFactory.createTiles();
    }

    /**
     * Retrieves a tile by its index.
     *
     * @param index the position on the board
     * @return the corresponding tile or null if not found
     */
    public Tile getTile(int index) {
        Optional<Tile> tile = tiles.stream().filter(t -> t.getIndex() == index).findFirst();
        return tile.orElse(null);
    }

    /**
     * Retrieves all tiles on the board.
     *
     * @return list of all tiles
     */
    public List<Tile> getTiles() {
        return tiles;
    }
}
