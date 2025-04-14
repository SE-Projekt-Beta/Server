package at.aau.serg.websocketdemoserver.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Player {
    private final String id;   // Eindeutige ID (UUID)
    private final String username;
    private int position = 0;
    private int money = 1500;

    public Player(String username) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
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
}