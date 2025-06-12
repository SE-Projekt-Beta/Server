package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

public class PlayerDTO {

    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String nickname;

    public PlayerDTO() {
        // Leerer Konstruktor für Deserialisierung (z. B. Gson oder Jackson)
    }

    public PlayerDTO(int id, String nickname) {
        this.id = id;
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
