package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.model.board.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
// tile factory import


public class Player implements Comparable<Player> {

    private static int idCounter = 1;

    @Getter
    private final int id;
    @Setter
    @Getter
    private String nickname;
    @Setter
    @Getter
    private Tile currentTile;
    @Getter
    private int cash;
    private boolean alive;
    private final List<StreetTile> ownedStreets = new ArrayList<>();
    @Getter
    private int suspensionRounds;
    private boolean hasEscapeCard;
    @Getter
    @Setter
    private boolean hasRolledDice = false;
    private final GameBoard board;

    public Player(String nickname, GameBoard board) {
        this.id = idCounter++;
        this.currentTile = new SpecialTile(1, "START", TileType.START);
        this.nickname = nickname;
        this.cash = 3000;
        this.alive = true;
        this.board = board;
    }

    public Player(int id, String nickname, GameBoard board) {
        this.id = id;
        this.currentTile = new SpecialTile(1, "START", TileType.START);
        this.nickname = nickname;
        this.cash = 3000;
        this.alive = true;
        this.board = board;
        if (id >= idCounter) {
            idCounter = id + 1;
        }
    }

    public void setCash(int newCash) {
        this.cash = newCash;
        if (this.cash < 0) {
            eliminate();
        }
    }

    public void addCash(int amount) {
        setCash(this.cash + amount);
    }

    public void deductCash(int amount) {
        setCash(this.cash - amount);
    }
    public boolean adjustCash(int delta) {
        this.cash += delta;
        if (this.cash < 0) {
            eliminate();
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void eliminate() {
        this.alive = false;
        this.cash = 0;
        // alle Streets freigeben
        for (StreetTile street : ownedStreets) {
            street.setOwner(null);
            street.clearBuildings();
        }
        ownedStreets.clear();
    }

    public boolean hasEscapeCard() {
        return hasEscapeCard;
    }

    public void setEscapeCard(boolean hasEscapeCard) {
        this.hasEscapeCard = hasEscapeCard;
    }

    public boolean isSuspended() {
        return suspensionRounds > 0;
    }

    public void decreaseSuspension() {
        if (suspensionRounds > 0) {
            suspensionRounds--;
        }
    }

    public void suspendForRounds(int rounds) {
        this.suspensionRounds = rounds;
    }

    public List<StreetTile> getOwnedStreets() {
        return new ArrayList<>(ownedStreets);
    }

    public boolean purchaseStreet(int position) {
        Tile tile = board.getTile(position);
        if (!(tile instanceof StreetTile street)) return false;
        if (street.getOwner() != null || street.getPrice() > cash) return false;

        deductCash(street.getPrice());
        street.setOwner(this);
        ownedStreets.add(street);
        return true;
    }

    public boolean sellStreet(int position) {
        Tile tile = board.getTile(position);
        if (!(tile instanceof StreetTile street)) return false;
        if (!ownedStreets.contains(street)) return false;

        addCash(street.calculateSellValue());
        street.setOwner(null);
        street.clearBuildings();
        ownedStreets.remove(street);
        return true;
    }

    public void moveToTile(int index) {
        if (!isSuspended()) {
            Tile destination = board.getTile(index % board.getTiles().size());
            this.currentTile = destination;
        }
    }

    public void moveSteps(int steps) {
        if (!isSuspended()) {
            int currentIndex = (currentTile != null) ? currentTile.getIndex() : -1;
            int totalTiles = board.getTiles().size();
            int newIndex = (currentIndex + steps) % totalTiles;
            moveToTile(newIndex);
            setHasRolledDice(true);
        }
    }

    public int calculateWealth() {
        int total = cash;
        for (StreetTile street : ownedStreets) {
            total += street.getPrice();
            total += street.getHouseCost() * street.getBuildings().stream().filter(b -> b == BuildingType.HOUSE).count();
            total += street.getHotelCost() * street.getBuildings().stream().filter(b -> b == BuildingType.HOTEL).count();
        }
        return total;
    }

    public void transferCash(Player receiver, int amount) {
        this.deductCash(amount);
        receiver.addCash(amount);
    }

    public static void resetIdCounter() {
        idCounter = 1;
    }

    @Override
    public int compareTo(Player other) {
        return Integer.compare(this.id, other.id);
    }
}
