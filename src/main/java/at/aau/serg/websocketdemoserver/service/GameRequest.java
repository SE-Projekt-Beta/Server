package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;

import java.util.List;


public interface GameRequest {
    /**
     * Führt eine Spielfunktion aus und gibt das Ergebnis als GameMessage zurück.
     * Zusätzliche Nachrichten (z. B. Kartenaktionen) werden über extraMessages gesammelt.
     *
     * @param lobbyId        die ID der Lobby
     * @param payload        das vom Client gesendete JSON-Objekt (z. B. mit playerId)
     * @param gameState      der aktuelle GameState, der ggf. verändert wird
     * @param extraMessages  Liste zusätzlicher Nachrichten (z. B. Spieler hat Karte gezogen)
     * @return Hauptnachricht mit aktualisiertem GameState oder Fehler
     */
    GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages);
}

