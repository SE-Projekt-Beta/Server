package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import at.aau.serg.websocketdemoserver.service.GameRequest;
import at.aau.serg.websocketdemoserver.service.MessageFactory;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RollDiceRequest implements GameRequest {

    private final Dice dice;

    public RollDiceRequest(Dice dice) {
        this.dice = dice;
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

            // check if already rolled dice
            if (player.isHasRolledDice()) {
                System.out.println("Player " + player.getNickname() + " has already rolled the dice.");
                return MessageFactory.error(lobbyId, "Du hast bereits geworfen.");
            }

            // get current tile
            Tile currentTile = player.getCurrentTile();
            System.out.println("Player " + player.getNickname() + " is on tile: " + (currentTile != null ? currentTile.getType() : "null"));

            int steps = dice.roll();
            System.out.println("Player " + player.getNickname() + " rolled a " + steps);
            player.moveSteps(steps);

            // check what the player has landed on
            Tile newTile = player.getCurrentTile();

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

                    } else {
                        System.out.println("Player " + player.getNickname() + " landed on an unowned street.");
                    }

                    // send a message to ask if the player wants to buy the property
                    extraMessages.add(new GameMessage(
                            lobbyId,
                            MessageType.ASK_BUY_PROPERTY,
                            new JSONObject().put("playerId", playerId).put("fieldIndex", newTile.getIndex()).toMap()
                    ));
                    break;
                case GOTO_JAIL:
                    System.out.println("Player " + player.getNickname() + " landed on GOTO_JAIL.");
                    break;
                case BANK:
                    System.out.println("Player " + player.getNickname() + " landed on a bank tile.");
                    break;
                case RISK:
                    System.out.println("Player " + player.getNickname() + " landed on a risk tile.");
                    player.setHasRolledDice(false);
                    break;
                case TAX:
                    System.out.println("Player " + player.getNickname() + " landed on a tax tile.");
                    break;
                default:
                    System.out.println("Player " + player.getNickname() + " landed on an unknown tile type.");
            }



            extraMessages.add(new GameMessage(
                    lobbyId,
                    MessageType.DICE_ROLLED,
                    new JSONObject().put("playerId", playerId).put("steps", steps).toMap()
            ));

            // Kein advanceTurn() hier – Zug ist **noch nicht beendet**.
            return MessageFactory.gameState(lobbyId, gameState);

        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(lobbyId, "Fehler beim Würfeln: " + e.getMessage());
        }
    }
}
