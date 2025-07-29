package at.aau.serg.websocketdemoserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmoteRateLimiterTest {
    private EmoteRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new EmoteRateLimiter();
    }

    @Test
    void testCanSend_UnderLimit_AllowsSending() {
        String player = "Alice";
        // Die ersten drei sollten erlaubt sein
        assertTrue(rateLimiter.canSend(player));
        assertTrue(rateLimiter.canSend(player));
        assertTrue(rateLimiter.canSend(player));
    }

    @Test
    void testCanSend_ExceedsLimit_BlocksSending() {
        String player = "Bob";

        // 3 erlaubte Emotes
        assertTrue(rateLimiter.canSend(player));
        assertTrue(rateLimiter.canSend(player));
        assertTrue(rateLimiter.canSend(player));

        // 4. sollte blockiert werden
        assertFalse(rateLimiter.canSend(player));
    }

    @Test
    void testCanSend_AfterTimeWindow_AllowsAgain() throws InterruptedException {
        String player = "Charlie";

        // 3 Emotes senden
        assertTrue(rateLimiter.canSend(player));
        assertTrue(rateLimiter.canSend(player));
        assertTrue(rateLimiter.canSend(player));

        // Direkt danach gesperrt
        assertFalse(rateLimiter.canSend(player));

        // Wartezeit: > 60 Sekunden
        Thread.sleep(61_000);

        // Sollte wieder erlaubt sein
        assertTrue(rateLimiter.canSend(player));
    }

    @Test
    void testMultiplePlayersIndependentLimits() {
        String alice = "Alice";
        String bob = "Bob";

        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimiter.canSend(alice));
            assertTrue(rateLimiter.canSend(bob));
        }

        assertFalse(rateLimiter.canSend(alice));
        assertFalse(rateLimiter.canSend(bob));
    }
}
