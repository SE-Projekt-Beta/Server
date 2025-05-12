package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.BankCardDrawnPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.cards.BankCard;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class DrawBankCardRequest implements GameRequest {

    private final BankCardDeck bankCardDeck;

    public DrawBankCardRequest(BankCardDeck bankCardDeck) {
        this.bankCardDeck = bankCardDeck;
    }

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) payload;
            JSONObject obj = new JSONObject(map);

            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder ausgeschieden.");
            }

            // Karte ziehen
            BankCard card = bankCardDeck.drawCard();
            int amount = card.getAmount();
            String description = card.getDescription();

            // Geld anwenden
            boolean isBankrupt = player.adjustCash(amount);

            // Spezifische Nachricht mit vollständigem Payload
            BankCardDrawnPayload payloadToSend = new BankCardDrawnPayload(
                    player.getId(),
                    amount,
                    player.getCash(),
                    description
            );
            extraMessages.add(new GameMessage(lobbyId, MessageType.DRAW_BANK_CARD, payloadToSend));

            if (isBankrupt) {
                extraMessages.add(MessageFactory.playerLost(lobbyId, player.getId()));
            }

            // Runde beenden
            gameState.advanceTurn();

            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Ziehen der Bankkarte: " + e.getMessage());
        }
    }
}
