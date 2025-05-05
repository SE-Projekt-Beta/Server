package at.aau.serg.websocketdemoserver.dto;

import at.aau.serg.websocketdemoserver.dto.PropertyEntry;

import java.util.List;

public class PropertyListUpdatePayload {
    private int playerId;
    private List<PropertyEntry> ownedStreets;

    public PropertyListUpdatePayload() {}

    public PropertyListUpdatePayload(int playerId, List<PropertyEntry> ownedStreets) {
        this.playerId = playerId;
        this.ownedStreets = ownedStreets;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public List<PropertyEntry> getOwnedStreets() {
        return ownedStreets;
    }

    public void setOwnedStreets(List<PropertyEntry> ownedStreets) {
        this.ownedStreets = ownedStreets;
    }
}
