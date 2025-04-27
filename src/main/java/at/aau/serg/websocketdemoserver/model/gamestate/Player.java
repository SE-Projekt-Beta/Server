package at.aau.serg.websocketdemoserver.model.gamestate;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;

import java.util.ArrayList;
import java.util.List;

public class Player implements Comparable<Player> {

    private static int idCounter = 1;

    private final int id;
    private String nickname;
    private Tile currentTile;
    private int cash;
    private final List<StreetTile> ownedStreets = new ArrayList<>();
    private int suspensionRounds;
    private boolean hasEscapeCard;
    private boolean cheatFlag;

    public Player(String nickname) {
        this.id = idCounter++;
        this.nickname = nickname;
        this.cash = 3000; // Startkapital
        this.suspensionRounds = 0;
        this.hasEscapeCard = false;
        this.cheatFlag = false;
    }

    public Player() {
        this("Player-" + idCounter);
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
        return hasEscapeCard;
    }

    public void setEscapeCard(boolean hasCard) {
        this.hasEscapeCard = hasCard;
    }

    public boolean isSuspended() {
        return suspensionRounds > 0;
    }

    public void decreaseSuspension() {
        if (suspensionRounds > 0) {
            suspensionRounds--;
        }
    }

    public void resetSuspension() {
        suspensionRounds = 0;
    }

    public void suspendForRounds(int rounds) {
        suspensionRounds = rounds;
    }

    public int getSuspensionRounds() {
        return suspensionRounds;
    }

    public List<StreetTile> getOwnedStreets() {
        return new ArrayList<>(ownedStreets);
    }

    public boolean purchaseStreet(int position) {
        Tile tile = GameState.getBoard().getTile(position);
        if (!(tile instanceof StreetTile street)) return false;
        if (street.getOwner() != null) return false;
        if (street.getPrice() > cash) return false;

        cash -= street.getPrice();
        street.setOwner(this);
        ownedStreets.add(street);
        return true;
    }

    public boolean sellStreet(int position) {
        Tile tile = GameState.getBoard().getTile(position);
        if (!(tile instanceof StreetTile street)) return false;
        if (street.getOwner() == null || street.getOwner().getId() != this.id) return false;

        cash += street.calculateSellValue();
        street.setOwner(null);
        street.clearBuildings();
        ownedStreets.remove(street);
        return true;
    }

    public void moveToTile(int index) {
        if (!isSuspended()) {
            Tile destination = GameState.getBoard().getTile(index);
            if (destination != null) {
                this.currentTile = destination;
            }
        }
    }

    public void moveSteps(int steps) {
        if (!isSuspended()) {
            int totalTiles = GameState.getBoard().getTiles().size();
            int newIndex = (currentTile.getIndex() + steps) % (totalTiles + 1);
            moveToTile(newIndex);
        }
    }

    public int calculateWealth() {
        int total = cash;
        for (StreetTile street : ownedStreets) {
            total += street.getPrice();
            total += street.getHouseCost() * street.getBuildings().stream().filter(b -> b == at.aau.serg.websocketdemoserver.model.board.BuildingType.HOUSE).count();
            total += street.getHotelCost() * street.getBuildings().stream().filter(b -> b == at.aau.serg.websocketdemoserver.model.board.BuildingType.HOTEL).count();
        }
        return total;
    }

    public void transferCash(Player receiver, int amount) {
        if (this.cash >= amount) {
            this.cash -= amount;
            receiver.cash += amount;
        }
    }

    public boolean hasCheated() {
        return cheatFlag;
    }

    public void setCheatFlag(boolean cheatFlag) {
        this.cheatFlag = cheatFlag;
    }

    public static void resetIdCounter() {
        idCounter = 1;
    }

    @Override
    public int compareTo(Player other) {
        return Integer.compare(this.id, other.id);
    }
}
