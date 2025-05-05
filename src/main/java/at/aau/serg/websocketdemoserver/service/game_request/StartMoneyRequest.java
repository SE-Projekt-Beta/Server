package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.StartMoneyPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class StartMoneyRequest implements GameHandlerInterface {

    private static final int BASE_BONUS = 200;

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        // PlayerId und Faktor kommen über das Payload
        StartMoneyPayload payload = message.parsePayload(StartMoneyPayload.class);
        int playerId = payload.getPlayerId();
        int factor = payload.getBonusAmount(); // hier steht der Multiplikator (1 für überquert, 2 für genau auf Start)

        Player player = gameState.getPlayer(playerId);
        if (player == null) {
            return GameMessage.error("Spieler nicht gefunden.");
        }

        int bonus = BASE_BONUS * factor;
        int newCash = player.getCash() + bonus;
        player.setCash(newCash);

        StartMoneyPayload resultPayload = new StartMoneyPayload();
        resultPayload.setPlayerId(playerId);
        resultPayload.setBonusAmount(bonus);
        resultPayload.setNewCash(newCash);

        return new GameMessage(MessageType.START_MONEY, resultPayload);
    }
}
