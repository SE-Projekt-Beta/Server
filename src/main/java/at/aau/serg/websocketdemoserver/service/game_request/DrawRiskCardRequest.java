package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.cards.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class DrawRiskCardRequest implements GameRequest {

    private final RiskCardDeck deck;
    private final Tile jailTile;

    public DrawRiskCardRequest(RiskCardDeck deck, Tile jailTile) {
        this.deck = deck;
        this.jailTile = jailTile;
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

            RiskCard card = deck.drawCard();
            String title = card.getTitle();
            String description = card.getDescription();

            if (card instanceof CashRiskCard cashCard) {
                int amount = cashCard.getAmount();
                boolean isBankrupt = player.adjustCash(amount);

                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.DRAW_RISK_CARD,
                        new RiskCardDrawnPayload(player.getId(), amount, player.getCash(), title, description)
                ));

                if (isBankrupt) {
                    extraMessages.add(MessageFactory.playerLost(lobbyId, player.getId()));
                }

            } else if (card instanceof EscapeRiskCard) {
                player.setEscapeCard(true);

                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.DRAW_RISK_CARD,
                        new RiskCardDrawnPayload(player.getId(), 0, player.getCash(), title, description)
                ));

                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.PLAYER_OUT_OF_JAIL_CARD,
                        new PlayerOutOfJailCardPayload(player.getId(), player.getNickname())
                ));

            } else if (card instanceof GoToJailRiskCard) {
                if (player.hasEscapeCard()) {
                    player.setEscapeCard(false);
                    extraMessages.add(new GameMessage(
                            lobbyId,
                            MessageType.DRAW_RISK_CARD,
                            new RiskCardDrawnPayload(player.getId(), 0, player.getCash(),
                                    "Freiheitskarte verwendet",
                                    "Du hast eine Freiheitskarte eingesetzt und musst nicht ins Gefängnis.")
                    ));
                } else {
                    player.setCurrentTile(jailTile);
                    player.suspendForRounds(3);

                    extraMessages.add(new GameMessage(
                            lobbyId,
                            MessageType.DRAW_RISK_CARD,
                            new RiskCardDrawnPayload(player.getId(), 0, player.getCash(), title, description)
                    ));
                }
            }

            gameState.advanceTurn();
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Ziehen der Risiko-Karte: " + e.getMessage());
        }
    }
}
