package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.LobbyMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.LobbyHandlerInterface;

public class InitPlayerRequest implements LobbyHandlerInterface {

    @Override
    public LobbyMessage execute(GameState gameState, Object parameter) {
        String nickname = (String) parameter;
        Player player = new Player(nickname, gameState.getBoard());
        gameState.addPlayer(player);

        String[] payload = new String[] {
                String.valueOf(player.getId()),
                player.getNickname()
        };

        return new LobbyMessage(LobbyMessageType.PLAYER_INIT, payload);
    }
}
