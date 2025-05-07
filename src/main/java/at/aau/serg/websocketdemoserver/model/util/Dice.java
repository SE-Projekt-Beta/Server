package at.aau.serg.websocketdemoserver.model.util;

import java.util.Random;

public class Dice {

    private final int min;
    private final int max;
    private final Random random;

    public Dice(int min, int max) {
        this.min = min;
        this.max = max;
        this.random = new Random();
    }

    /**
     * Rolls the dice and returns a random number between min and max.
     *
     * @return the result of the dice roll
     */
    public int roll() {
        return random.nextInt((max - min) + 1) + min;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}