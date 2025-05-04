package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.model.util.Dice;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class RollDiceRequest implements GameHandlerInterface {

    private final Dice dice = new Dice(1, 6);

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        RollDicePayload payload = message.parsePayload(RollDicePayload.class);
        int playerId = payload.getPlayerId();

        Player player = gameState.getPlayer(playerId);
        if (player == null) {
            return GameMessage.error("Spieler nicht gefunden.");
        }

        if (gameState.getCurrentPlayer().getId() != playerId) {
            return GameMessage.error("Nicht dein Zug.");
        }

        if (player.isSuspended()) {
            int suspension = player.getSuspensionRounds();
            player.decreaseSuspension();

            SkippedTurnPayload skipped = new SkippedTurnPayload(
                    playerId,
                    "Du musst aussetzen."
            );
            skipped.setTilePos(player.getCurrentTile().getIndex());
            skipped.setTileName(player.getCurrentTile().getLabel());
            skipped.setSuspension(suspension - 1);

            gameState.advanceTurn();
            return new GameMessage(MessageType.SKIPPED_TURN, skipped);
        }

        int roll = dice.roll();
        int targetIndex = player.calculateNewPosition(roll);

        // Wir setzen den Move aber noch nicht um (Tile-Ereignis folgt separat)
        RollDiceResultPayload result = new RollDiceResultPayload();

        MovePlayerPayload move = new MovePlayerPayload();
        move.setPlayerId(playerId);
        move.setDice(roll);
        move.setPos(targetIndex);

        result.setMove(move);

        CurrentPlayerPayload next = new CurrentPlayerPayload();
        next.setPlayerId(gameState.peekNextPlayer().getId()); // Vorschau
        result.setNext(next);

        return new GameMessage(MessageType.DICE_ROLLED, result);
    }
}
