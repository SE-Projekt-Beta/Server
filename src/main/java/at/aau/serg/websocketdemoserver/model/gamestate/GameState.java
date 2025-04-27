package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.dto.PlayerDTO;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import java.util.*;

public class GameState {

    private final GameBoard board;
    private final List<Player> turnOrder; // Reihenfolge der Spieler
    private final Map<Integer, Player> playersById; // ID -> Player
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

    public void addPlayers(List<PlayerDTO> playerDTOs) {
        turnOrder.clear();
        playersById.clear();
        Player.resetIdCounter();

        for (PlayerDTO dto : playerDTOs) {
            Player player = new Player(dto.getNickname(), this.board);
            playersById.put(dto.getId(), player); // Player-ID bleibt DTO-ID
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
