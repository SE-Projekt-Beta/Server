package at.aau.serg.websocketdemoserver.dto;
import lombok.Data;

@Data
public class BuildHousePayload {
    private int playerId;
    private int tilePos;
}

