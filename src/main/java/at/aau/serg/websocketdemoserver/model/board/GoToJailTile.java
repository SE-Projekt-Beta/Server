package at.aau.serg.websocketdemoserver.model.board;

public class GoToJailTile extends SpecialTile {
    public GoToJailTile(int index) {
        super(index, "Polizeikontrolle", TileType.GOTO_JAIL);
    }
}
