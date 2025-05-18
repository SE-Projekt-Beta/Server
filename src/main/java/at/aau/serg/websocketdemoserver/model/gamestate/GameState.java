package at.aau.serg.websocketdemoserver.model.gamestate;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class GameState {

    @Getter
    private final GameBoard board;
    private final List<Player> turnOrder;
    private final Map<Integer, Player> playersById;
    private int currentPlayerIndex;
    @Getter
    private int currentRound;
    private final List<Player> rankingList;

    public GameState() {
        this.board = new GameBoard();
        this.turnOrder = new ArrayList<>();
        this.playersById = new HashMap<>();
        this.currentPlayerIndex = 0;
        this.currentRound = 1;
        this.rankingList = new ArrayList<>();
    }

    /**
     * Initialisiert das Spiel mit zufälliger Spielerreihenfolge.
     */
    public void startGame(List<Player> players) {
        resetGame();
        Collections.shuffle(players);
        for (Player player : players) {
            playersById.put(player.getId(), player);
            turnOrder.add(player);
        }
    }

    /**
     * Gibt den aktuellen Spieler.
     */
    public Player getCurrentPlayer() {
        if (turnOrder.isEmpty()) return null;
        return turnOrder.get(currentPlayerIndex);
    }

    public int getCurrentPlayerId() {
        Player current = getCurrentPlayer();
        return current != null ? current.getId() : -1;
    }

    public Player getPlayer(int id) {
        return playersById.get(id);
    }

    public Collection<Player> getAllPlayers() {
        return playersById.values();
    }

    public List<Player> getAlivePlayers() {
        return turnOrder.stream()
                .filter(Player::isAlive)
                .toList(); // Java 16+
    }

    /**
     * Führt den Rundenwechsel durch – überspringt gesperrte oder ausgeschiedene Spieler.
     */
    public void advanceTurn() {
        if (turnOrder.isEmpty()) return;

        int initialIndex = currentPlayerIndex;
        int loopCounter = 0;

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.size();
            if (currentPlayerIndex == 0 && currentPlayerIndex != initialIndex) {
                currentRound++;
            }
            Player current = getCurrentPlayer();
            current.setHasRolledDice(false);

            // Wenn Spieler im Gefängnis: Runde runterzählen, überspringen
            if (current.isAlive() && current.getSuspensionRounds() > 0) {
                current.decreaseSuspension();
                loopCounter++;
                return;
            }

            // Nur lebende und nicht gesperrte Spieler dürfen spielen
            if (current.isAlive() && current.getSuspensionRounds() == 0) {
                return;
            }

            if (!current.isAlive()) {
                // set money to -1
                current.setCash(-1);
            }

            loopCounter++;
        } while (loopCounter < turnOrder.size());
    }


    /**
     * Liefert das Ranking (nach Reichtum) zurück.
     */
    public List<Player> getRankingList() {
        rankingList.clear();
        rankingList.addAll(playersById.values());
        rankingList.sort(Comparator.comparingInt(Player::calculateWealth).reversed());
        return rankingList;
    }

    /**
     * Überprüft, ob das Spiel beendet ist (nur bei aktiviertem Rundensystem).
     */
    public boolean isGameOver(int maxRounds, boolean roundsModeEnabled) {
        return roundsModeEnabled && currentRound > maxRounds;
    }

    public void resetGame() {
        Player.resetIdCounter();
        turnOrder.clear();
        playersById.clear();
        currentPlayerIndex = 0;
        currentRound = 1;
    }

    /**
     * Nützlich für Clients: Prüft, ob der angegebene Spieler an der Reihe ist.
     */
    public boolean isPlayersTurn(int playerId) {
        return getCurrentPlayerId() == playerId;
    }
}
