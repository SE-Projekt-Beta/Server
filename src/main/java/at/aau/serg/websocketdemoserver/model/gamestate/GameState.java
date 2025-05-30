package at.aau.serg.websocketdemoserver.model.gamestate;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class GameState {

    private final GameBoard board;
    private final List<Player> turnOrder;
    private final Map<Integer, Player> playersById;
    private int currentPlayerIndex;
    @Getter
    private int currentRound;
    private final List<Player> rankingList;

    @JsonIgnore
    private final Set<Integer> tilesBoughtThisTurn = new HashSet<>();

    @JsonIgnore
    private final Set<Integer> tilesBuiltThisTurn = new HashSet<>();

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
                .toList();
    }

    public GameBoard getBoard() {
        return board;
    }

    public void advanceTurn() {
        if (turnOrder.isEmpty()) return;

        int initialIndex = currentPlayerIndex;
        int loopCounter = 0;

        // Felder zurücksetzen
        tilesBoughtThisTurn.clear();
        tilesBuiltThisTurn.clear();

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.size();
            if (currentPlayerIndex == 0 && currentPlayerIndex != initialIndex) {
                currentRound++;
            }

            Player current = getCurrentPlayer();
            current.setHasRolledDice(false);

            if (current.isAlive() && current.getSuspensionRounds() > 0) {
                current.decreaseSuspension();
                loopCounter++;
                return;
            }

            if (current.isAlive() && current.getSuspensionRounds() == 0) {
                return;
            }

            loopCounter++;
        } while (loopCounter < turnOrder.size());
    }

    public List<Player> getRankingList() {
        rankingList.clear();
        rankingList.addAll(playersById.values());
        rankingList.sort(Comparator.comparingInt(Player::calculateWealth).reversed());
        return rankingList;
    }

    public boolean isGameOver(int maxRounds, boolean roundsModeEnabled) {
        return roundsModeEnabled && currentRound > maxRounds;
    }

    public void resetGame() {
        Player.resetIdCounter();
        turnOrder.clear();
        playersById.clear();
        currentPlayerIndex = 0;
        currentRound = 1;
        tilesBoughtThisTurn.clear();
        tilesBuiltThisTurn.clear();
    }

    public boolean isPlayersTurn(int playerId) {
        return getCurrentPlayerId() == playerId;
    }

    // ==== Hausbau-Logik ====

    public void markTileBoughtThisTurn(int tilePos) {
        tilesBoughtThisTurn.add(tilePos);
    }

    public boolean wasTileBoughtThisTurn(int tilePos) {
        return tilesBoughtThisTurn.contains(tilePos);
    }

    public void markTileBuiltThisTurn(int tilePos) {
        tilesBuiltThisTurn.add(tilePos);
    }

    public boolean wasTileBuiltThisTurn(int tilePos) {
        return tilesBuiltThisTurn.contains(tilePos);
    }
}
