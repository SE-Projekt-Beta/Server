package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

public class LobbyMessage {
    private LobbyMessageType type;
    @Getter
    @Setter
    private String lobbyId;
    private Object payload;

    public LobbyMessage() {}

    // existing constructors might need to be overloaded:
    public LobbyMessage(LobbyMessageType type, Object payload) {
        this.type    = type;
        this.payload = payload;
    }

    public LobbyMessage(LobbyMessageType type, String lobbyId, Object payload) {
        this.type = type;
        this.lobbyId = lobbyId;
        this.payload = payload;
    }

    public LobbyMessageType getType() {
        return type;
    }

    public void setType(LobbyMessageType type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
