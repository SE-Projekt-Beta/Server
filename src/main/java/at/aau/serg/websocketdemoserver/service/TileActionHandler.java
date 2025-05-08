package at.aau.serg.websocketdemoserver.service;


import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.*;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;


import java.util.Map;

public class TileActionHandler {

    private final EventCardService eventCardService;

    public TileActionHandler(EventCardService eventCardService) {
        this.eventCardService = eventCardService;
    }

    public GameMessage handleTileLanding(Player player, Tile tile) {
        if (player == null || tile == null) {
            return createErrorMessage("Ungültiger Spieler oder Spielfeld.");
        }

        if (tile instanceof RiskTile) {
            return handleRiskTile(player);
        }

        if (tile instanceof BankTile) {
            return handleBankTile(player);
        }

        if (tile instanceof GoToJailTile) {
            return handleGoToJail(player);
        }

        if (tile instanceof StreetTile streetTile) {
            return handleStreetTile(player, streetTile);
        }

        return createSkippedMessage(player.getId(), tile);
    }

    private GameMessage handleRiskTile(Player player) {
        var riskCard = eventCardService.drawRiskCard();
        riskCard.execute(player);

        return new GameMessage(
                MessageType.DRAW_EVENT_RISIKO_CARD,
                Map.of(
                        "title", riskCard.getTitle(),
                        "description", riskCard.getDescription()
                )
        );
    }

    private GameMessage handleBankTile(Player player) {
        var bankCard = eventCardService.drawBankCard();
        bankCard.execute(player);

        return new GameMessage(
                MessageType.DRAW_EVENT_BANK_CARD,
                Map.of(
                        "title", bankCard.getTitle(),
                        "description", bankCard.getDescription()
                )
        );
    }

    private GameMessage handleGoToJail(Player player) {
        player.suspendForRounds(3);
        player.moveToTile(10); // Annahme: 10 = Gefängnis

        return new GameMessage(
                MessageType.GO_TO_JAIL,
                Map.of(
                        "playerId", player.getId(),
                        "tilePos", 10,
                        "tileName", "Gefängnis"
                )
        );
    }

    private GameMessage handleStreetTile(Player player, StreetTile street) {
        Player owner = street.getOwner();
        if (owner != null && owner.getId() != player.getId()) {
            int rent = street.calculateRent();
            player.transferCash(owner, rent);

            return new GameMessage(
                    MessageType.MUST_PAY_RENT,
                    Map.of(
                            "playerId", player.getId(),
                            "ownerId", owner.getId(),
                            "tilePos", street.getIndex(),
                            "tileName", street.getLabel(),
                            "amount", rent
                    )
            );
        }

        if (owner == null) {
            return new GameMessage(
                    MessageType.CAN_BUY_PROPERTY,
                    Map.of(
                            "playerId", player.getId(),
                            "tilePos", street.getIndex(),
                            "tileName", street.getLabel(),
                            "price", street.getPrice()
                    )
            );
        }

        return createSkippedMessage(player.getId(), street);
    }

    private GameMessage createSkippedMessage(int playerId, Tile tile) {
        return new GameMessage(
                MessageType.SKIPPED,
                Map.of(
                        "playerId", playerId,
                        "tilePos", tile.getIndex(),
                        "tileName", tile.getLabel()
                )
        );
    }

    private GameMessage createErrorMessage(String error) {
        return new GameMessage(
                MessageType.ERROR,
                error
        );
    }
}