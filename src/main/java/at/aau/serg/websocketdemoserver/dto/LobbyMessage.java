package at.aau.serg.websocketdemoserver.dto;

public class LobbyMessage {
    private LobbyMessageType type;
    private Object payload;

    public LobbyMessage() {}

    public LobbyMessage(LobbyMessageType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public LobbyMessageType getType() { return type; }

    public void setType(LobbyMessageType type) { this.type = type; }

    public Object getPayload() { return payload; }

    public void setPayload(Object payload) { this.payload = payload; }
}
