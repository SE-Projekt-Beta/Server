package at.aau.serg.websocketdemoserver.dto;

/**
 * A message about lobby‐level events, now carrying lobbyId.
 */
public class LobbyMessage {
    private int lobbyId;
    private LobbyMessageType type;
    private Object payload;

    public LobbyMessage() { }

    /** For per‐lobby messages */
    public LobbyMessage(int lobbyId, LobbyMessageType type, Object payload) {
        this.lobbyId = lobbyId;
        this.type    = type;
        this.payload = payload;
    }

    /** For global messages (no lobbyId needed) */
    public LobbyMessage(LobbyMessageType type, Object payload) {
        this.type    = type;
        this.payload = payload;
    }

    public int getLobbyId() { return lobbyId; }
    public void setLobbyId(int lobbyId) { this.lobbyId = lobbyId; }

    public LobbyMessageType getType() { return type; }
    public void setType(LobbyMessageType type) { this.type = type; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}