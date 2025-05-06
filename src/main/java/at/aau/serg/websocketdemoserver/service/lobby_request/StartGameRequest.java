package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartGameRequest implements LobbyHandlerInterface {

    private final GameHandler gameHandler;

    public StartGameRequest(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public LobbyMessageType getType() {
        return LobbyMessageType.START_GAME;
    }

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        String lobbyId = gameState.getId();
        if (!gameState.isReadyToStart()) {
            return new LobbyMessage(
                    LobbyMessageType.ERROR,
                    "Mindestens 2 Spieler notwendig, um das Spiel zu starten."
            );
        }

        gameState.startGame();
        gameHandler.initGame(gameState.getPlayers());

        List<PlayerLobbyEntry> players = gameState.getPlayers().stream()
                .map(p -> new PlayerLobbyEntry(p.getId(), p.getNickname()))
                .toList();

        GameStartedPayload payload = new GameStartedPayload(players);
        return new LobbyMessage(LobbyMessageType.START_GAME, payload);
    }
}
