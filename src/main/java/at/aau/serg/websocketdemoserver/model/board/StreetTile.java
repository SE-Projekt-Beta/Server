package at.aau.serg.websocketdemoserver.model.board;

import at.aau.serg.websocketdemoserver.model.gamestate.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreetTile extends Tile {

    private final int price;
    private final int baseRent;
    private final StreetLevel level;
    private final int houseCost;
    private final int hotelCost;

    private final List<BuildingType> buildings = new ArrayList<>();
    private Player owner;

    public StreetTile(int index, String label, int price, int baseRent, StreetLevel level, int houseCost) {
        this(index, label, price, baseRent, level, houseCost, houseCost * 2);
    }

    public StreetTile(int index, String label, int price, int baseRent, StreetLevel level, int houseCost, int hotelCost) {
        super(index);
        setLabel(label);
        this.price = price;
        this.baseRent = baseRent;
        this.level = level;
        this.houseCost = houseCost;
        this.hotelCost = hotelCost;
    }

    public int calculateRent() {
        if (buildings.isEmpty()) return baseRent;

        double factor = switch (level) {
            case CHEAP -> 0.5;
            case NORMAL -> 1.0;
            case PREMIUM -> 1.5;
        };

        if (buildings.contains(BuildingType.HOTEL)) {
            return (int) (baseRent + baseRent * factor * 6);
        }

        return (int) (baseRent + baseRent * buildings.size() * factor);
    }

    public boolean addHouse() {
        if (buildings.contains(BuildingType.HOTEL)) return false;
        if (buildings.size() >= 4) return false;
        buildings.add(BuildingType.HOUSE);
        return true;
    }

    public boolean addHotel() {
        if (buildings.contains(BuildingType.HOTEL)) return false;
        if (getHouseCount() < 4) return false;
        buildings.clear();
        buildings.add(BuildingType.HOTEL);
        return true;
    }

    public void clearBuildings() {
        buildings.clear();
    }

    public int getHouseCount() {
        return Collections.frequency(buildings, BuildingType.HOUSE);
    }

    public int getHotelCount() {
        return Collections.frequency(buildings, BuildingType.HOTEL);
    }

    public int calculateRawValue() {
        return price + getHouseCount() * houseCost + getHotelCount() * hotelCost;
    }

    public int calculateSellValue() {
        double value = price * 0.5;
        value += getHouseCount() * houseCost * 0.25;
        value += getHotelCount() * hotelCost * 0.25;
        return (int) value;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public int getPrice() {
        return price;
    }

    public int getBaseRent() {
        return baseRent;
    }

    public StreetLevel getLevel() {
        return level;
    }

    public int getHouseCost() {
        return houseCost;
    }

    public int getHotelCost() {
        return hotelCost;
    }

    public List<BuildingType> getBuildings() {
        return new ArrayList<>(buildings);
    }

    @Override
    public TileType getType() {
        return TileType.STREET;
    }

    public boolean buildHouse() {
        return addHouse();
    }

    public boolean buildHotel() {
        return addHotel();
    }

}
