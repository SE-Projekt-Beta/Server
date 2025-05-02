package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

public class JoinLobbyRequest implements LobbyHandlerInterface {

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        String nickname = (String) parameter;
        Player newPlayer = new Player(nickname, gameState.getBoard());
        gameState.addPlayer(newPlayer);
        return new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, gameState.getPlayers());
    }
}
