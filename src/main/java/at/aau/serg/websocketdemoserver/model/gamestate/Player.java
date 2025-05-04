package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.dto.PlayerLostPayload;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final int id;
    private final String nickname;
    private Tile currentTile;
    private int cash;
    private boolean escapeCard;
    private int suspensionRounds;
    private final List<StreetTile> ownedStreets;

    public Player(int id, String nickname, Tile startTile) {
        this.id = id;
        this.nickname = nickname;
        this.currentTile = startTile;
        this.cash = 1500;
        this.escapeCard = false;
        this.suspensionRounds = 0;
        this.ownedStreets = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile tile) {
        this.currentTile = tile;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public boolean hasEscapeCard() {
        return escapeCard;
    }

    public void setEscapeCard(boolean hasCard) {
        this.escapeCard = hasCard;
    }

    public boolean isSuspended() {
        return suspensionRounds > 0;
    }

    public int getSuspensionRounds() {
        return suspensionRounds;
    }

    public void suspendForRounds(int rounds) {
        this.suspensionRounds = rounds;
    }

    public void decreaseSuspension() {
        if (suspensionRounds > 0) {
            suspensionRounds--;
        }
    }

    public void resetSuspension() {
        this.suspensionRounds = 0;
    }

    public void moveToTile(int index, GameBoard board) {
        this.currentTile = board.getTile(index);
    }

    public void moveSteps(int steps, GameBoard board) {
        int currentIndex = currentTile.getIndex();
        int newIndex = (currentIndex + steps) % board.getTiles().size();
        this.currentTile = board.getTile(newIndex);
    }

    public boolean purchaseStreet(int tileIndex) {
        if (!(currentTile instanceof StreetTile street)) return false;
        if (street.getIndex() != tileIndex) return false;
        if (street.getOwner() != null) return false;
        if (cash < street.getPrice()) return false;

        this.cash -= street.getPrice();
        street.setOwner(this);
        ownedStreets.add(street);
        return true;
    }

    public GameMessage transferCash(Player to, int amount) {
        this.cash -= amount;
        to.cash += amount;
        if (this.cash < 0) {
            return createBankruptMessage();
        }
        return null;
    }

    public GameMessage deductCash(int amount) {
        this.cash -= amount;
        if (this.cash < 0) {
            return createBankruptMessage();
        }
        return null;
    }

    private GameMessage createBankruptMessage() {
        PlayerLostPayload payload = new PlayerLostPayload();
        payload.setPlayerId(this.id);
        payload.setCash(this.cash);
        payload.setReason("Bankrott! Dein Vermögen ist negativ.");
        return new GameMessage(MessageType.PLAYER_LOST, payload);
    }

    public List<StreetTile> getOwnedStreets() {
        return new ArrayList<>(ownedStreets);
    }

    public int calculateWealth() {
        int propertyValue = ownedStreets.stream()
                .mapToInt(st -> st.getPrice()
                        + st.getHouseCount() * st.getHouseCost()
                        + st.getHotelCount() * st.getHotelCost())
                .sum();
        return cash + propertyValue;
    }

    public void resetProperties() {
        for (StreetTile st : ownedStreets) {
            st.setOwner(null);
            st.clearBuildings();
        }
        ownedStreets.clear();
    }

    public int calculateNewPosition(int steps) {
        int size = GameBoard.get().getTiles().size();
        int current = this.currentTile.getIndex();
        return (current + steps) % size;
    }
}
