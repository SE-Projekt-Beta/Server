package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.GoToJailPayload;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.board.JailTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class GoToJailRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        int playerId = gameState.getCurrentPlayer().getId();
        Player player = gameState.getPlayer(playerId);

        // Finde das Gefängnisfeld
        Tile jailTile = GameBoard.get().getTiles().stream()
                .filter(t -> t instanceof JailTile)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Kein Gefängnisfeld gefunden."));

        player.moveToTile(jailTile.getIndex(), GameBoard.get());
        player.suspendForRounds(2);

        GoToJailPayload payload = new GoToJailPayload(
                player.getId(),
                jailTile.getIndex(),
                player.getSuspensionRounds(),
                "Du wurdest ins Gefängnis geschickt."
        );

        return new GameMessage(MessageType.GO_TO_JAIL, payload);
    }
}
