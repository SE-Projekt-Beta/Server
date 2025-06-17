package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameRequest;

import java.util.List;

public class EndGameRequest implements GameRequest {

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        // 1. Rangliste berechnen
        List<Player> ranking = gameState.getRankingList();
        List<PlayerResultDTO> rankedDtos = ranking.stream()
                .map(p -> new PlayerResultDTO(p.getId(), p.getNickname(), p.calculateWealth()))
                .toList();

        // 2. Nachricht mit Rangliste zurückgeben
        GameEndPayload endPayload = new GameEndPayload(rankedDtos);
        return new GameMessage(
                lobbyId,
                MessageType.GAME_ENDED,
                endPayload
        );
    }
}
