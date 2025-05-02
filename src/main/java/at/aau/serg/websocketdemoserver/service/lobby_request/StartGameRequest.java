package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

public class StartGameRequest implements LobbyHandlerInterface {

    private final GameHandler gameHandler;

    public StartGameRequest(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        if (!gameState.isReadyToStart()) {
            return new LobbyMessage(LobbyMessageType.ERROR, "Mindestens 2 Spieler notwendig, um das Spiel zu starten.");
        }
        gameState.startGame();
        gameHandler.initGame(gameState.getPlayers());
        return new LobbyMessage(LobbyMessageType.START_GAME, gameState.getPlayers());
    }
}
