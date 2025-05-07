package at.aau.serg.websocketdemoserver.dto;


public class PlayerDTO {
    private int id;
    private String nickname;

    public PlayerDTO(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }
}

