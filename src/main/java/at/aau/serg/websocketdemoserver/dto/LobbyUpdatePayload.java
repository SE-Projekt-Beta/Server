package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class LobbyUpdatePayload {
    private List<String> usernames;

    public LobbyUpdatePayload() {}

    public LobbyUpdatePayload(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
}
