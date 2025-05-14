package at.aau.serg.websocketdemoserver.model.board;

import at.aau.serg.websocketdemoserver.model.util.Dice;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    @Test
    void testConstructorAndGetters() {
        Dice dice = new Dice(1, 6);
        assertEquals(1, dice.getMin());
        assertEquals(6, dice.getMax());
    }

    @RepeatedTest(100)
    void testRollWithinRange() {
        Dice dice = new Dice(1, 6);
        int result = dice.roll();
        assertTrue(result >= 1 && result <= 6, "Wurf liegt außerhalb des Bereichs: " + result);
    }
}
