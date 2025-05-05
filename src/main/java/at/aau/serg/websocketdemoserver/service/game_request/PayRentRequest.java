package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PayRentPayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class PayRentRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        Player current = gameState.getCurrentPlayer();
        Tile tile = current.getCurrentTile();

        if (!(tile instanceof StreetTile street)) {
            return GameMessage.error("Aktuelles Feld ist keine bebaubare Straße.");
        }

        Player owner = street.getOwner();
        if (owner == null || owner.getId() == current.getId()) {
            return GameMessage.error("Keine Miete zu zahlen – entweder eigenes Feld oder ohne Besitzer.");
        }

        int rent = street.calculateRent();

        // Zahlung mit Bankrottprüfung
        GameMessage bankruptcyCheck = current.transferCash(owner, rent);
        if (bankruptcyCheck != null) {
            return bankruptcyCheck;
        }

        PayRentPayload payload = new PayRentPayload();
        payload.setFromPlayerId(current.getId());
        payload.setToPlayerId(owner.getId());
        payload.setAmount(rent);

        return new GameMessage(MessageType.RENT_PAID, payload);
    }
}
