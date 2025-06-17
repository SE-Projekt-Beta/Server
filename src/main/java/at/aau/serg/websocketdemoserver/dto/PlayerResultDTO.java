package at.aau.serg.websocketdemoserver.dto;

public class PlayerResultDTO {
    private final int id;
    private final String nickname;
    private final int wealth;

    public PlayerResultDTO(int id, String nickname, int wealth) {
        this.id = id;
        this.nickname = nickname;
        this.wealth = wealth;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getWealth() {
        return wealth;
    }

    @Override
    public String toString() {
        return nickname + " (" + wealth + "$)";
    }
}
