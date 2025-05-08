package at.aau.serg.websocketdemoserver.model.cards;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.CashTaskPayload;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;

public class CashBankCard extends BankCard {
    private final int amount;

    public CashBankCard(int id, String title, String description, int amount) {
        super(id, title, description);
        this.amount = amount;
    }

    @Override
    public GameMessage execute(Player player) {
        int oldCash = player.getCash();
        player.setCash(oldCash + amount);

        CashTaskPayload payload = new CashTaskPayload(player.getId(), amount, player.getCash());
        return new GameMessage(MessageType.CASH_TASK, payload);
    }
}