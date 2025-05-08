package at.aau.serg.websocketdemoserver.dto;

public class PlayerDTO {

    private int id;
    private String nickname;

    public PlayerDTO() {
        // Leerer Konstruktor für Deserialisierung (z. B. Gson oder Jackson)
    }

    public PlayerDTO(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
