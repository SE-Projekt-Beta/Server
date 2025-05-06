package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class LobbyManager {
    private final ArrayList<GameState> gameStates = new ArrayList<>();

    public GameState createLobby(String name) {
        GameState gameState = new GameState(name);
        gameStates.add(gameState);
        return gameState;
    }

    public ArrayList<GameState> listLobbies() {
        return gameStates;
    }

    public GameState getLobby(String lobbyId) {
        for (GameState gameState : gameStates) {
            if (gameState.getLobbyId().equals(lobbyId)) {
                return gameState;
            }
        }
        return null;
    }
}
