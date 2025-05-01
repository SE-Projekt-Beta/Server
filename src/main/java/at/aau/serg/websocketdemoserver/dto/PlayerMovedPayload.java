package at.aau.serg.websocketdemoserver.dto;

import at.aau.serg.websocketdemoserver.model.board.TileType;


/**
 * Payload für die Information, dass ein Spieler sich auf ein anderes Spielfeld bewegt hat.
 */
public class PlayerMovedPayload {

    private int playerId;
    private int position;
    private int diceRoll;
    private String tileName;
    private TileType tileType;

    public PlayerMovedPayload() {
        // Standard-Konstruktor für Deserialisierung
    }

    public PlayerMovedPayload(int playerId, int position, int diceRoll, String tileName, TileType tileType) {
        this.playerId = playerId;
        this.position = position;
        this.diceRoll = diceRoll;
        this.tileName = tileName;
        this.tileType = tileType;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }
}