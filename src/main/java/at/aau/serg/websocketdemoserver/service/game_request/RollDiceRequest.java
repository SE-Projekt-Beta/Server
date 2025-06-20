package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.RiskCardDrawnPayload;
import at.aau.serg.websocketdemoserver.dto.WentToJailPayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.cards.RiskCardDeck;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.DicePair;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import at.aau.serg.websocketdemoserver.model.cards.BankCardDeck;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RollDiceRequest implements GameRequest {

    private final DicePair dicePair;

    public RollDiceRequest(DicePair dicePair) {
        this.dicePair = dicePair;
    }

    @Override
    public GameMessage execute(int lobbyId, Object payload, GameState gameState, List<GameMessage> extraMessages) {
        try {
            JSONObject obj = new JSONObject((Map<?, ?>) payload);
            int playerId = obj.getInt("playerId");

            Player player = gameState.getPlayer(playerId);
            if (player == null) {
                System.out.println("Player with ID " + playerId + " not found.");
                return MessageFactory.error(lobbyId, "Spieler nicht gefunden.");
            }

            if (playerId != gameState.getCurrentPlayerId()) {
                System.out.println("Player " + player.getNickname() + " tried to roll the dice, but it's not their turn.");
                return MessageFactory.error(lobbyId, "Nicht dein Zug.");
            }

            // check how many players are alive
            if (gameState.getAlivePlayers().size() <= 1) {
                System.out.println("Game over, only one player left.");
                return MessageFactory.gameOver(lobbyId, gameState.getCurrentPlayer());
            }


            // check if already rolled dice
            if (player.isHasRolledDice()) {
                System.out.println("Player " + player.getNickname() + " has already rolled the dice.");
                // check current player
                System.out.println("Current player: " + gameState.getCurrentPlayer().getNickname());
                return MessageFactory.error(lobbyId, "Du hast bereits geworfen.");
            }
            // Merken, dass Spieler gerade gewürfelt hat
            player.setHasRolledDice(true);

            if (!player.isAlive()) {
                return MessageFactory.error(lobbyId, "Spieler ungültig oder ausgeschieden.");
            }

            // get current tile
            Tile currentTile = player.getCurrentTile();
            // print name of street if street
            System.out.println("Player " + player.getNickname() + " is on tile: " + currentTile.getIndex() + " " + currentTile.getType() + " " + (currentTile instanceof StreetTile ? ((StreetTile) currentTile).getLabel() : ""));

            if (player.isSuspended()) {
                System.out.println("Player " + player.getNickname() + " is suspended for " + player.getSuspensionRounds() + " rounds.");
                // ask for payment
                extraMessages.add(new GameMessage(
                        lobbyId,
                        MessageType.ASK_PAY_PRISON,
                        Map.of(
                            "playerId", playerId,
                            "suspensionRounds", player.getSuspensionRounds()
                        )
                ));

                return MessageFactory.gameState(lobbyId, gameState);
            }

            // save previous index for start check
            int prevIndex = currentTile.getIndex();

            int[] rolls = dicePair.roll();
            int steps1 = rolls[0];
            int steps2 = rolls[1];
            int totalSteps = steps1 + steps2;
            System.out.println("Player " + player.getNickname() + " rolled a " + totalSteps);
            player.moveSteps(totalSteps);

            // check what the player has landed on
            Tile newTile = player.getCurrentTile();
            int newIndex = newTile.getIndex();

            // check if passed or landed on START (field 1)
            if (prevIndex > newIndex || newIndex == 1) {
                System.out.println("Player " + player.getNickname() + " passed or landed on START.");
                GameRequest passedStart = new PassedStartRequest();
                GameMessage result = passedStart.execute(lobbyId, payload, gameState, extraMessages);

                // if player landed exactly on START, turn already ended there
                if (newIndex == 1) {
                    System.out.println("Player " + player.getNickname() + " landed directly on START.");
                    extraMessages.add(new GameMessage(
                            lobbyId,
                            MessageType.DICE_ROLLED,
                            Map.of(
                                "playerId", playerId,
                                "roll1", steps1,
                                "roll2", steps2,
                                "fieldIndex", newIndex
                            )
                    ));
                    return result;
                }
            }

            switch (newTile.getType()) {
                case STREET:
                    System.out.println("Player " + player.getNickname() + " landed on a street.");

                    // cast to StreetTile
                    StreetTile streetTile = (StreetTile) newTile;

                    // check if the street is owned
                    if (streetTile.getOwner() != null) {
                        System.out.println("Player " + player.getNickname() + " landed on a street owned by " + streetTile.getOwner().getNickname());

                        // transfer rent
                        Player owner = streetTile.getOwner();
                        int rent = streetTile.calculateRent();
                        player.transferCash(owner, rent);
                        System.out.println("Player " + player.getNickname() + " paid rent of " + rent + " to " + owner.getNickname());
                        gameState.advanceTurn();
                    } else {

                        // check if the player can buy the street
                        if (player.getCash() < streetTile.getPrice()) {
                            System.out.println("Player " + player.getNickname() + " cannot afford the street.");
                            extraMessages.add(new GameMessage(
                                    lobbyId,
                                    MessageType.EXTRA_MESSAGE,
                                    Map.of(
                                        "playerId", playerId,
                                        "title", "Straße kaufen",
                                        "message", "Du kannst dir diese Straße nicht leisten."
                                    )
                            ));
                            gameState.advanceTurn();
                            break;
                        }

                        System.out.println("Player " + player.getNickname() + " landed on an unowned street.");
                        extraMessages.add(new GameMessage(
                                lobbyId,
                                MessageType.ASK_BUY_PROPERTY,
                                Map.of(
                                    "playerId", playerId,
                                    "fieldIndex", newTile.getIndex()
                                )
                        ));
                    }

                    break;
                case GOTO_JAIL:

                    Tile jailTile = gameState.getBoard().getTile(31);

                    if (player.hasEscapeCard()) {
                        player.setEscapeCard(false);
                        extraMessages.add(new GameMessage(
                                lobbyId,
                                MessageType.DRAW_RISK_CARD,
                                new RiskCardDrawnPayload(
                                        player.getId(),
                                        0,
                                        player.getCash(),
                                        "Freiheitskarte verwendet",
                                        "Du hast eine Freiheitskarte genutzt und musst nicht ins Gefängnis.")
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

                    gameState.advanceTurn();
                    System.out.println("Player " + player.getNickname() + " landed on GOTO_JAIL.");
                    break;
                case BANK:
                    System.out.println("Player " + player.getNickname() + " landed on a bank tile.");
                    DrawBankCardRequest drawBankCard = new DrawBankCardRequest(BankCardDeck.get());
                    return drawBankCard.execute(lobbyId, payload, gameState, extraMessages);
                case RISK:
                    System.out.println("Player " + player.getNickname() + " landed on a risk tile.");
                    DrawRiskCardRequest drawRiskCard = new DrawRiskCardRequest(RiskCardDeck.get());
                    return drawRiskCard.execute(lobbyId, payload, gameState, extraMessages);
                case TAX:
                    System.out.println("Player " + player.getNickname() + " landed on a tax tile.");
                    return new PayTaxRequest().execute(lobbyId, payload, gameState, extraMessages);
                default:
                    System.out.println("Player " + player.getNickname() + " landed on an unknown tile type: " + newTile.getType());
                    gameState.advanceTurn();
            }

            extraMessages.add(new GameMessage(
                    lobbyId,
                    MessageType.DICE_ROLLED,
                    Map.of(
                        "playerId", playerId,
                        "roll1", steps1,
                        "roll2", steps2,
                        "fieldIndex", newTile.getIndex()
                    )
            ));

            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            return MessageFactory.error(lobbyId, "Fehler beim Würfeln: " + e.getMessage());
        }
    }
}
