package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;

import java.util.*;

public class GameState {

    private final GameBoard board;
    private final List<Player> turnOrder;
    private final Map<Integer, Player> playersById;
    private final List<Player> winnerRankingList;

    private int currentPlayerIndex;
    private int currentRound;
    private int roundStartId;

    public GameState() {
        this.board = new GameBoard();
        this.turnOrder = new ArrayList<>();
        this.playersById = new HashMap<>();
        this.winnerRankingList = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentRound = 1;
        this.roundStartId = -1;
    }

    public void setPlayers(List<Player> players) {
        turnOrder.clear();
        playersById.clear();
        Player.resetIdCounter();

        for (Player player : players) {
            playersById.put(player.getId(), player);
            turnOrder.add(player);
        }
    }

    public Player getCurrentPlayer() {
        if (turnOrder.isEmpty()) return null;
        return turnOrder.get(currentPlayerIndex);
    }

    public Player getPlayer(int id) {
        return playersById.get(id);
    }

    public Collection<Player> getAllPlayers() {
        return playersById.values();
    }

    public List<Player> getTurnOrder() {
        return Collections.unmodifiableList(turnOrder);
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

        if (roundStartId == -1) {
            roundStartId = getCurrentPlayer().getId();
        } else if (roundStartId == getCurrentPlayer().getId()) {
            currentRound++;
        }
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean isGameOver(int maxRounds, boolean roundsModeEnabled) {
        return roundsModeEnabled && currentRound > maxRounds;
    }

    public List<Player> getRankingList() {
        winnerRankingList.clear();
        winnerRankingList.addAll(playersById.values());
        winnerRankingList.sort(Comparator.comparingInt(Player::calculateWealth).reversed());
        return winnerRankingList;
    }

    public void removePlayer(int id) {
        Player removed = playersById.remove(id);
        if (removed != null) {
            turnOrder.remove(removed);
        }
    }

    public void resetGame() {
        Player.resetIdCounter();
        turnOrder.clear();
        playersById.clear();
        winnerRankingList.clear();
        currentPlayerIndex = 0;
        currentRound = 1;
        roundStartId = -1;
    }
}
