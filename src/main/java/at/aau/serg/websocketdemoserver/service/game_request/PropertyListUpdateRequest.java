package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PropertyEntry;
import at.aau.serg.websocketdemoserver.dto.PropertyListUpdatePayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

import java.util.List;

public class PropertyListUpdateRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        int playerId = message.parsePayload(Integer.class);

        Player player = gameState.getPlayer(playerId);
        if (player == null) {
            return GameMessage.error("Spieler nicht gefunden.");
        }

        List<StreetTile> owned = gameState.getOwnedProperties(player);

        List<PropertyEntry> entries = owned.stream()
                .map(tile -> {
                    PropertyEntry entry = new PropertyEntry();
                    entry.setTileIndex(tile.getIndex());
                    entry.setTileName(tile.getName());
                    entry.setPrice(tile.getPrice());
                    entry.setBaseRent(tile.getBaseRent());
                    entry.setOwnerId(tile.getOwnerId());
                    entry.setOwnerName(tile.getOwnerName());
                    entry.setHouseCount(tile.getHouseCount());
                    entry.setHotelCount(tile.getHotelCount());
                    return entry;
                })
                .toList();

        PropertyListUpdatePayload payload = new PropertyListUpdatePayload(playerId, entries);
        return new GameMessage(MessageType.PROPERTY_LIST_UPDATE, payload);
    }

}
