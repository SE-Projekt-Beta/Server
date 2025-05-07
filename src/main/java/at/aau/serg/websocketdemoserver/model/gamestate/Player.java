package at.aau.serg.websocketdemoserver.model.gamestate;

import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.board.BuildingType;

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
    private final GameBoard board;

    // Konstruktor mit automatisch generierter ID
    public Player(String nickname, GameBoard board) {
        this.id = idCounter++;
        this.nickname = nickname;
        this.cash = 3000;
        this.board = board;
    }

    // Neuer Konstruktor mit übergebener fixer ID (z. B. aus DTO)
    public Player(int id, String nickname, GameBoard board) {
        this.id = id;
        this.nickname = nickname;
        this.cash = 3000;
        this.board = board;
        // WICHTIG: idCounter hochsetzen, damit keine ID-Kollision passiert
        if (id >= idCounter) {
            idCounter = id + 1;
        }
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

    public int getSuspensionRounds() {
        return suspensionRounds;
    }

    public List<StreetTile> getOwnedStreets() {
        return new ArrayList<>(ownedStreets);
    }

    public boolean purchaseStreet(int position) {
        Tile tile = board.getTile(position);
        if (!(tile instanceof StreetTile street)) return false;
        if (street.getOwner() != null || street.getPrice() > cash) return false;

        cash -= street.getPrice();
        street.setOwner(this);
        ownedStreets.add(street);
        return true;
    }

    public boolean sellStreet(int position) {
        Tile tile = board.getTile(position);
        if (!(tile instanceof StreetTile street)) return false;
        if (!ownedStreets.contains(street)) return false;

        cash += street.calculateSellValue();
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
            int currentIndex = (currentTile != null) ? currentTile.getIndex() : 0;
            int totalTiles = board.getTiles().size();
            int newIndex = (currentIndex + steps) % totalTiles;
            moveToTile(newIndex);
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
