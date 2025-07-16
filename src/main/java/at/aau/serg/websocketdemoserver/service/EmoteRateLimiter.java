package at.aau.serg.websocketdemoserver.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// legt fest, wie viele Emotes ein Spieler in einem bestimmten Zeitraum senden darf (max. 3 Emotes pro Minute)

public class EmoteRateLimiter {
    private static final int MAX_EMOTES = 3;
    private static final long TIME_WINDOW_MS = 60_000;

    private final Map<String, LinkedList<Long>> emoteTimestamps = new HashMap<>();

    public synchronized boolean canSend(String playerName) {
        long now = System.currentTimeMillis();
        LinkedList<Long> timestamps = emoteTimestamps.getOrDefault(playerName, new LinkedList<>());

        // Alte Timestamps entfernen
        timestamps.removeIf(ts -> now - ts > TIME_WINDOW_MS);

        // Limit prüfen
        if (timestamps.size() >= MAX_EMOTES) {
            return false;
        }

        // Zulassen und neuen Timestamp hinzufügen
        timestamps.add(now);
        emoteTimestamps.put(playerName, timestamps);
        return true;
    }
}
