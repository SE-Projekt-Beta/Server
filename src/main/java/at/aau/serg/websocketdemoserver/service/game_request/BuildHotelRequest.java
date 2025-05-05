package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.BuildPropertyPayload;
import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

public class BuildHotelRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        BuildPropertyPayload payload = message.parsePayload(BuildPropertyPayload.class);
        Player player = gameState.getPlayer(payload.getPlayerId());
        if (player == null) return GameMessage.error("Spieler nicht gefunden.");

        Tile tile = player.getCurrentTile();
        if (!(tile instanceof StreetTile street)) return GameMessage.error("Kein baubares Grundstück.");

        if (street.getOwner() != player) return GameMessage.error("Du besitzt dieses Grundstück nicht.");
        if (street.getHouseCount() < 4) return GameMessage.error("Du benötigst 4 Häuser für ein Hotel.");
        if (player.getCash() < street.getHotelCost()) return GameMessage.error("Nicht genug Geld für ein Hotel.");

        if (!street.buildHotel()) return GameMessage.error("Hotelbau nicht möglich.");

        player.setCash(player.getCash() - street.getHotelCost());

        return new GameMessage(MessageType.PROPERTY_LIST_UPDATE, gameState.getOwnedProperties(player));
    }
}
