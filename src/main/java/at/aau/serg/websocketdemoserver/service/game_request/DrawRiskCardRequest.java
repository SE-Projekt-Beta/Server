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

    public DrawRiskCardRequest(RiskCardDeck deck) {
        this.deck = deck;
    }

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            JSONObject obj = new JSONObject((Map<?, ?>) payload);
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null || !player.isAlive()) {
                return MessageFactory.error(lobbyId, "Ungültiger Spieler oder Spieler bereits ausgeschieden.");
            }

            RiskCard card = deck.drawCard();
            RiskCardEffect effect = card.getEffect();

            switch (effect) {
                case CASH:
                    int amount = ((CashRiskCard) card).getAmount();
                    boolean isBankrupt = player.adjustCash(amount);

                    extraMessages.add(new GameMessage(
                            lobbyId,
                            MessageType.DRAW_RISK_CARD,
                            new RiskCardDrawnPayload(
                                    playerId,
                                    amount,
                                    player.getCash(),
                                    card.getTitle(),
                                    card.getDescription()
                            )
                    ));

                    if (isBankrupt) {
                        extraMessages.add(MessageFactory.playerLost(lobbyId, playerId));
                    }

                    break;

                case GOTO_JAIL:
                    Tile jailTile = gameState.getBoard().getTile(31);

                    if (player.hasEscapeCard()) {
                        player.setEscapeCard(false);
                        player.setCurrentTile(null);
                        player.setSuspensionRounds(0);

                        extraMessages.add(new GameMessage(
                                lobbyId,
                                MessageType.PLAYER_OUT_OF_JAIL_CARD,
                                new PlayerOutOfJailCardPayload(playerId, "Spieler nutzt Aus-dem-Gefängnis-frei-Karte")
                        ));
                    } else {
                        player.setCurrentTile(jailTile);
                        player.suspendForRounds(3);

                        extraMessages.add(new GameMessage(
                                lobbyId,
                                MessageType.GO_TO_JAIL,
                                new WentToJailPayload(playerId)
                        ));
                    }
                    break;

                case ESCAPE_CARD:
                    player.setEscapeCard(true);
                    extraMessages.add(new GameMessage(
                            lobbyId,
                            MessageType.DRAW_RISK_CARD,
                            new RiskCardDrawnPayload(
                                    playerId,
                                    0,
                                    player.getCash(),
                                    card.getTitle(),
                                    card.getDescription()
                            )
                    ));
                    break;
            }

            gameState.advanceTurn();
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Ziehen der Risikokarte: " + e.getMessage());
        }
    }
}
