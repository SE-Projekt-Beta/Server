package at.aau.serg.websocketdemoserver.model.board;

public abstract class Tile {

    private final int index;
    private String label = "";

    public Tile(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}