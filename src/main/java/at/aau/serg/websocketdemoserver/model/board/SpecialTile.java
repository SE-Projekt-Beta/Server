package at.aau.serg.websocketdemoserver.model.board;

public class SpecialTile extends Tile {

    private final TileType type;

    public SpecialTile(int index, String label, TileType type) {
        super(index);
        setLabel(label);
        this.type = type;
    }

    @Override
    public TileType getType() {
        return type;
    }
}