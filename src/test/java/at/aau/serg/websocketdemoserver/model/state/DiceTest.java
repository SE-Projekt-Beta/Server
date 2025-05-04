package at.aau.serg.websocketdemoserver.model.state;

import at.aau.serg.websocketdemoserver.model.util.Dice;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiceTest {

    @Test
    void testGetMinMax() {
        Dice dice = new Dice(1, 6);
        assertEquals(1, dice.getMin());
        assertEquals(6, dice.getMax());
    }

    @RepeatedTest(100)
    void testRollWithinRange() {
        Dice dice = new Dice(1, 6);
        int result = dice.roll();
        assertTrue(result >= 1 && result <= 6, "Roll out of bounds: " + result);
    }
}

