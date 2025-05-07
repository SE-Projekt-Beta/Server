package at.aau.serg.websocketdemoserver.model.board;

public class SpecialTile extends Tile {

    public SpecialTile(int index) {
        super(index);
    }

    public SpecialTile(int index, String label) {
        super(index);
        setLabel(label);
    }
}