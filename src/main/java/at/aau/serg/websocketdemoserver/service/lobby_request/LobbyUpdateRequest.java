package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLobbyEntry;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyUpdateRequest implements LobbyHandlerInterface {

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        List<PlayerLobbyEntry> payload = gameState.getPlayers().stream()
                .map(p -> new PlayerLobbyEntry(p.getId(), p.getNickname()))
                .collect(Collectors.toList());

        return new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, payload);
    }
}
