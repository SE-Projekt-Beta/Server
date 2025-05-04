package at.aau.serg.websocketdemoserver.dto;

import at.aau.serg.websocketdemoserver.model.board.StreetTile;

import java.util.List;

public class PropertyListUpdatePayload {
    private int playerId;
    private List<StreetTile> ownedStreets;

    public PropertyListUpdatePayload() {}

    public PropertyListUpdatePayload(int playerId, List<StreetTile> ownedStreets) {
        this.playerId = playerId;
        this.ownedStreets = ownedStreets;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public List<StreetTile> getOwnedStreets() {
        return ownedStreets;
    }

    public void setOwnedStreets(List<StreetTile> ownedStreets) {
        this.ownedStreets = ownedStreets;
    }
}
