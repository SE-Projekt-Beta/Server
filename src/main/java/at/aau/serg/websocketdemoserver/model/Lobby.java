package at.aau.serg.websocketdemoserver.model;

import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Lobby {
    private final String id;
    private String name;
    private final GameState gameState;
    private final GameHandler gameHandler;
    private final Set<String> playerIds = new LinkedHashSet<>();

    public Lobby(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.gameState = new GameState();
        this.gameHandler = new GameHandler(gameState);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public GameHandler getGameHandler() { return gameHandler; }
    public GameState getGameState() { return gameState; }

    public Set<String> getPlayerIds() { return playerIds; }
    public void addPlayer(String playerId) { playerIds.add(playerId); }
    public void removePlayer(String playerId) { playerIds.remove(playerId); }
}
