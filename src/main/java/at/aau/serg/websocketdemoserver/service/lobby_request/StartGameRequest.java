package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLobbyEntry;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandler;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

import java.util.List;
import java.util.stream.Collectors;

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

        List<PlayerLobbyEntry> payload = gameState.getPlayers().stream()
                .map(p -> new PlayerLobbyEntry(p.getId(), p.getNickname()))
                .collect(Collectors.toList());

        return new LobbyMessage(LobbyMessageType.START_GAME, payload);
    }
}
