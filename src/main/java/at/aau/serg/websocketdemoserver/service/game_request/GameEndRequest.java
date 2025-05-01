package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GameEndRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        List<Player> winners = gameState.getRankingList();

        StringBuilder ranking = new StringBuilder();
        for (Player p : winners) {
            ranking.append(String.format(Locale.getDefault(), "%s#!#%d;", p.getNickname(), p.calculateWealth()));
        }

        // Reset state (optional, je nach Serverlogik)
        gameState.resetGame();

        return new GameMessage(MessageType.END_GAME, Map.of("ranking", ranking.toString()));
    }
}