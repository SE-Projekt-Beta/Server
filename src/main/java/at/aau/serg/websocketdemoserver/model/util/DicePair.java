package at.aau.serg.websocketdemoserver.model.util;

public class DicePair {
    private final Dice dice1;
    private final Dice dice2;

    public DicePair(Dice dice1, Dice dice2) {
        this.dice1 = dice1;
        this.dice2 = dice2;
    }

    public int[] roll() {
        return new int[]{dice1.roll(), dice2.roll()};
    }
}
