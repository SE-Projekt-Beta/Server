package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A message about game‐level events, now carrying lobbyId.
 */
@Setter
@Getter
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

}