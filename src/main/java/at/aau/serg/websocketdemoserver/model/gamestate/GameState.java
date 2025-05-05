package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.dto.GameEndedPayload;
import at.aau.serg.websocketdemoserver.dto.GameEndedPayload.PlayerRankingEntry;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameState {

    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private boolean gameStarted = false;

    public void addPlayer(Player player) {
        if (!gameStarted) {
            players.add(player);
        }
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) return;
        players.remove(player);
        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }

    public Player peekNextPlayer() {
        if (players.isEmpty()) return null;
        int nextIndex = (currentPlayerIndex + 1) % players.size();
        return players.get(nextIndex);
    }

    public void advanceTurn() {
        if (!players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    public Player getPlayer(int id) {
        return players.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<StreetTile> getOwnedProperties(Player player) {
        List<StreetTile> owned = new ArrayList<>();
        for (Tile tile : GameBoard.get().getTiles()) {
            if (tile instanceof StreetTile st && st.getOwner() == player) {
                owned.add(st);
            }
        }
        return owned;
    }

    public boolean isReadyToStart() {
        return players.size() >= 2;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        if (!isReadyToStart()) return;

        Collections.shuffle(players);
        currentPlayerIndex = 0;
        players.forEach(p -> p.moveToTile(1, GameBoard.get())); // Start-Position
        gameStarted = true;
    }

    public void resetGame() {
        for (Player player : players) {
            player.resetProperties();
        }
        players.clear();
        currentPlayerIndex = 0;
        gameStarted = false;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    // 💡 NEU: Automatischer Check, ob Spiel zu Ende ist
    public GameMessage checkForGameEnd() {
        if (players.size() > 1) return null;

        Player winner = players.get(0);
        List<PlayerRankingEntry> ranking = new ArrayList<>();
        ranking.add(new PlayerRankingEntry(1, winner.getNickname(), winner.calculateWealth()));

        return new GameMessage(MessageType.END_GAME, new GameEndedPayload(ranking));
    }
    public void setPlayers(List<Player> newPlayers) {
        this.players.clear();
        this.players.addAll(newPlayers);
        this.currentPlayerIndex = 0;
        this.gameStarted = false;
    }

}
