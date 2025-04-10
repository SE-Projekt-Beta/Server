package at.aau.serg.websocketdemoserver.model;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private final String id;
    private int position = 0;
    private int money = 1500;
    private final Set<Integer> ownedProperties = new HashSet<>();

    public Player(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Set<Integer> getOwnedProperties() {
        return ownedProperties;
    }

    public void addProperty(int tilePos) {
        ownedProperties.add(tilePos);
    }
}
