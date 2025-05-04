package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.game_request.*;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameHandler {

    private final GameState gameState;
    private final Map<MessageType, GameHandlerInterface> handlerMap = new HashMap<>();
    private final List<GameMessage> extraMessages = new ArrayList<>();

    public GameHandler(GameState gameState) {
        this.gameState = gameState;
        registerHandlers();
    }

    private void registerHandlers() {
        handlerMap.put(MessageType.ROLL_DICE, new RollDiceRequest());
        handlerMap.put(MessageType.MOVE_PLAYER, new MovePlayerRequest());
        handlerMap.put(MessageType.START_MONEY, new StartMoneyRequest());

        handlerMap.put(MessageType.CAN_BUY_PROPERTY, new AskBuyPropertyRequest());
        handlerMap.put(MessageType.BUY_PROPERTY, new BuyPropertyRequest());

        handlerMap.put(MessageType.MUST_PAY_RENT, new PayRentRequest());
        handlerMap.put(MessageType.GO_TO_JAIL, new GoToJailRequest());
        handlerMap.put(MessageType.PRISON_TASK, new JailCardHandlingRequest());
        handlerMap.put(MessageType.CASH_TASK, new CashTaskHandlingRequest());
        handlerMap.put(MessageType.PAY_TAX, new PayTaxRequest());

        handlerMap.put(MessageType.PLAYER_OUT_OF_JAIL_CARD, new PlayerOutOfJailCardRequest());

        handlerMap.put(MessageType.PROPERTY_LIST_UPDATE, new PropertyListUpdateRequest());

        handlerMap.put(MessageType.DRAW_EVENT_BANK_CARD, new DrawEventCardRequest());
        handlerMap.put(MessageType.DRAW_EVENT_RISIKO_CARD, new DrawEventCardRequest());

        handlerMap.put(MessageType.BUILD_HOUSE, new BuildHouseRequest());
        handlerMap.put(MessageType.BUILD_HOTEL, new BuildHotelRequest());
    }

    public GameMessage handle(GameMessage message) {
        extraMessages.clear();

        if (message == null || message.getType() == null) {
            return GameMessage.error("Ungültige Nachricht.");
        }

        GameHandlerInterface handler = handlerMap.get(message.getType());
        if (handler == null) {
            return GameMessage.error("Unbekannter Nachrichtentyp: " + message.getType());
        }

        return handler.execute(gameState, message);
    }

    public void initGame(List<Player> players) {
        gameState.setPlayers(players);
        System.out.println("[INIT] Spiel gestartet mit " + players.size() + " Spieler(n).");
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<GameMessage> getExtraMessages() {
        return extraMessages;
    }
}
