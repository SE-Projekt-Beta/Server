package at.aau.serg.websocketdemoserver.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A message about lobby‐level events, now carrying lobbyId.
 */
@Setter
@Getter
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

}