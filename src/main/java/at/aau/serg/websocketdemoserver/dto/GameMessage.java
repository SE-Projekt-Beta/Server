package at.aau.serg.websocketdemoserver.dto;

/**
 * A message about game‐level events, now carrying lobbyId.
 */
public class GameMessage {
    private int lobbyId;
    private MessageType type;
    private Object payload;

    public GameMessage() { }

    /** Use this to send a message within a lobby */
    public GameMessage(int lobbyId, MessageType type, Object payload) {
        this.lobbyId = lobbyId;
        this.type    = type;
        this.payload = payload;
    }

    /** Legacy ctor (no lobbyId) – avoid and prefer the above */
    public GameMessage(MessageType type, Object payload) {
        this.type    = type;
        this.payload = payload;
    }

    public int getLobbyId() { return lobbyId; }
    public void setLobbyId(int lobbyId) { this.lobbyId = lobbyId; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}