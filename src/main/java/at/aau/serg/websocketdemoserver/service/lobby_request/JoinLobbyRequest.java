package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

import java.util.List;
import java.util.stream.Collectors;

public class JoinLobbyRequest implements LobbyHandlerInterface {

    @Override
    public LobbyMessageType getType() {
        return LobbyMessageType.JOIN_LOBBY;
    }

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        if (!(parameter instanceof JoinLobbyPayload payload)) {
            return new LobbyMessage(LobbyMessageType.ERROR, "Ungültiges Payload-Format.");
        }

        String nickname = payload.getNickname();
        if (nickname == null || nickname.isBlank()) {
            return new LobbyMessage(LobbyMessageType.ERROR, "Nickname fehlt.");
        }

        boolean exists = gameState.getPlayers().stream()
                .anyMatch(p -> p.getNickname().equalsIgnoreCase(nickname));

        if (exists) {
            return new LobbyMessage(LobbyMessageType.ERROR, "Nickname bereits vergeben.");
        }

        // Spieler wurde bereits mit InitPlayerRequest erstellt → nur Liste aktualisieren
        List<PlayerLobbyEntry> entries = gameState.getPlayers().stream()
                .map(p -> new PlayerLobbyEntry(p.getId(), p.getNickname()))
                .collect(Collectors.toList());

        LobbyUpdatePayload lobbyUpdatePayload = new LobbyUpdatePayload(entries);
        return new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, lobbyUpdatePayload);
    }
}
