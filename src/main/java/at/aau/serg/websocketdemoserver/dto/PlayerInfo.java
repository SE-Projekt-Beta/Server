package at.aau.serg.websocketdemoserver.dto;

public class PlayerInfo {
    private String id;
    private int position;
    private int money;

    public PlayerInfo() {}

    public PlayerInfo(String id, int position, int money) {
        this.id = id;
        this.position = position;
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
