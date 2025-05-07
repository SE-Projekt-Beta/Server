package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.dto.PlayerDTO;
import java.util.*;

public class GameState {

    private final GameBoard board;
    private final List<Player> turnOrder;
    private final Map<Integer, Player> playersById;
    private int currentPlayerIndex;
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
     * Startet das Spiel mit einer zufälligen Reihenfolge
     */
    public void startGame(List<Player> players) {
        turnOrder.clear();
        playersById.clear();
        Player.resetIdCounter();

        Collections.shuffle(players);
        for (Player player : players) {
            playersById.put(player.getId(), player);
            turnOrder.add(player);
        }

        currentPlayerIndex = 0;
        currentRound = 1;
    }

    public void setPlayers(List<Player> players) {
        turnOrder.clear();
        playersById.clear();
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
        Player p = getCurrentPlayer();
        return (p != null) ? p.getId() : -1;
    }

    public Player getPlayer(int id) {
        return playersById.get(id);
    }

    public Collection<Player> getAllPlayers() {
        return playersById.values();
    }

    public GameBoard getBoard() {
        return board;
    }

    public void advanceTurn() {
        if (turnOrder.isEmpty()) return;

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.size();
            if (currentPlayerIndex == 0) {
                currentRound++;
            }
        } while (getCurrentPlayer().isSuspended());
    }

    public int getCurrentRound() {
        return currentRound;
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
    }
}
