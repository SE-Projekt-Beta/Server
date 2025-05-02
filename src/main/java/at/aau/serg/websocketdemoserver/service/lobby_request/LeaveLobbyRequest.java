package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

public class LeaveLobbyRequest implements LobbyHandlerInterface {

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        String nickname = (String) parameter;
        Player toRemove = gameState.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);
        if (toRemove != null) {
            gameState.removePlayer(toRemove);
        }
        return new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, gameState.getPlayers());
    }
}
