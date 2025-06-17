package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class GameEndPayload {
    private final List<PlayerResultDTO> ranking;

    public GameEndPayload(List<PlayerResultDTO> ranking) {
        this.ranking = ranking;
    }

    public List<PlayerResultDTO> getRanking() {
        return ranking;
    }
}
