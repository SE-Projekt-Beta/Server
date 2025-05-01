package at.aau.serg.websocketdemoserver.service.game_request;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameHandlerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PropertyListUpdateRequest implements GameHandlerInterface {

    @Override
    public GameMessage execute(GameState gameState, GameMessage message) {
        List<String> propertyList = new ArrayList<>();

        for (Tile tile : gameState.getBoard().getTiles()) {
            if (tile instanceof StreetTile property) {
                Player owner = property.getOwner();
                String ownerName = (owner != null) ? owner.getNickname() : "-/-";

                String propertyEntry = String.format(Locale.getDefault(),
                        "%d#%d#%d#%s#%d",
                        property.getIndex(),
                        property.getPrice(),
                        property.getBaseRent(),
                        ownerName,
                        property.getHouses()
                );

                propertyList.add(propertyEntry);
            }
        }

        return new GameMessage(
                MessageType.PROPERTY_LIST_UPDATE,
                Map.of("entries", propertyList)
        );
    }
}

