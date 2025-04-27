package at.aau.serg.websocketdemoserver.model.gamestate;

import java.util.*;

public class GameState {

    private static GameBoard board = new GameBoard();
    private static HashMap<Integer, Player> players = new HashMap<>();
    private static int currentStartPlayerId = -1;
    private static int currentRound = 1;
    private static final List<Player> rankingList = new ArrayList<>();

    private GameState() {
        // Prevent instantiation
    }

    public static void setBoard(GameBoard board) {
        GameState.board = board;
    }

    public static GameBoard getBoard() {
        return board;
    }

    public static HashMap<Integer, Player> getPlayers() {
        return players;
    }

    public static int getCurrentStartPlayerId() {
        return currentStartPlayerId;
    }

    public static void setCurrentStartPlayerId(int id) {
        GameState.currentStartPlayerId = id;
    }

    public static int getCurrentRound() {
        return currentRound;
    }

    public static void setCurrentRound(int round) {
        GameState.currentRound = round;
    }

    public static void advanceRound(int playerId) {
        if (currentStartPlayerId == -1) {
            currentStartPlayerId = playerId;
        } else if (currentStartPlayerId == playerId) {
            currentRound++;
        }
    }

    public static boolean isGameOver(int maxRounds, boolean useRoundsMode) {
        return useRoundsMode && currentRound >= maxRounds;
    }

    public static void resetGame() {
        Player.resetIdCounter();
        board = new GameBoard();
        players = new HashMap<>();
        currentStartPlayerId = -1;
        currentRound = 1;
    }

    public static List<Player> getRankingList() {
        rankingList.clear();
        rankingList.addAll(players.values());
        rankingList.sort(Comparator.comparingInt(Player::calculateWealth).reversed());
        return rankingList;
    }
}
