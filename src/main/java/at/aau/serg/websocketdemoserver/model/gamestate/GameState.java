package at.aau.serg.websocketdemoserver.model.gamestate;

import java.util.*;

public class GameState {

    private final GameBoard board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int currentRound;

    public GameState(GameBoard board) {
        this.board = board;
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentRound = 1;
    }

    public synchronized Player addPlayer(String nickname) {
        Player newPlayer = new Player(nickname, this.board);
        players.add(newPlayer);
        return newPlayer;
    }

    public synchronized void addPlayer(Player player) {
        players.add(player);
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);
    }

    public synchronized List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public synchronized boolean isReadyToStart() {
        return players.size() >= 2;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) currentRound++;
    }

    public void startGame() {
        if (isReadyToStart()) {
            currentRound = 1;
        }
    }

    public GameBoard getBoard() {
        return board;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setPlayers(List<Player> newPlayers) {
        players.clear();
        players.addAll(newPlayers);
    }
}
