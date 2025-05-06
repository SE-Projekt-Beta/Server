package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.LobbyUpdatePayload;
import at.aau.serg.websocketdemoserver.dto.PlayerLobbyEntry;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JoinLobbyRequest implements LobbyHandlerInterface {

    private static int nextId = 1;

    @Override
    public LobbyMessageType getType() {
        return LobbyMessageType.JOIN_LOBBY;
    }

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        String lobbyId = gameState.getId();
        // parameter should be a map with "username"
        if (!(parameter instanceof Map<?, ?> payload) ||
                !(payload.get("username") instanceof String nickname) ||
                nickname.isBlank()) {
            return new LobbyMessage(
                    LobbyMessageType.ERROR,
                    lobbyId,
                    "Ungültiger Spielername."
            );
        }

        boolean exists = gameState.getPlayers().stream()
                .anyMatch(p -> p.getNickname().equalsIgnoreCase(nickname));
        if (exists) {
            return new LobbyMessage(
                    LobbyMessageType.ERROR,
                    lobbyId,
                    "Nickname bereits vergeben."
            );
        }

        Tile startTile = GameBoard.get().getTile(1);
        Player newPlayer = new Player(nextId++, nickname, startTile);

        List<PlayerLobbyEntry> entries = gameState.getPlayers().stream()
                .map(p -> new PlayerLobbyEntry(p.getId(), p.getNickname()))
                .collect(Collectors.toList());
        LobbyUpdatePayload update = new LobbyUpdatePayload(entries);

        return new LobbyMessage(
                LobbyMessageType.LOBBY_UPDATE,
                update
        );
    }
}
