package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.board.StreetTile;
import at.aau.serg.websocketdemoserver.model.board.Tile;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;

import java.util.*;
import java.util.stream.Collectors;

public class MessageFactory {

    public static GameMessage error(int lobbyId, String reason) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reason", reason);
        return new GameMessage(lobbyId, MessageType.ERROR, payload);
    }

    public static GameMessage gameState(int lobbyId, GameState gameState) {
        List<Map<String, Object>> players = gameState.getAllPlayers().stream()
                .map(MessageFactory::mapPlayer)
                .collect(Collectors.toList());

        List<Map<String, Object>> boardTiles = gameState.getBoard().getTiles().stream()
                .map(MessageFactory::mapTile)
                .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        System.out.println("Current player name: " + gameState.getCurrentPlayer().getNickname() + " ID: " + gameState.getCurrentPlayerId());
        payload.put("currentPlayerId", gameState.getCurrentPlayerId());

        Player current = gameState.getCurrentPlayer();
        if (current != null) {
            payload.put("currentPlayerName", current.getNickname());
        } else {
            payload.put("currentPlayerName", "X");
        }

        payload.put("players", players);
        payload.put("currentRound", gameState.getCurrentRound());
        payload.put("board", boardTiles);

        return new GameMessage(lobbyId, MessageType.GAME_STATE, payload);
    }


    public static GameMessage playerLost(int lobbyId, int playerId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", playerId);
        return new GameMessage(lobbyId, MessageType.PLAYER_LOST, payload);
    }

    public static GameMessage gameOver(int lobbyId, List<Integer> ranking) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ranking", ranking);
        return new GameMessage(lobbyId, MessageType.GAME_OVER, payload);
    }

    // ---------------------
    // Hilfsmethoden
    // ---------------------

    private static Map<String, Object> mapPlayer(Player p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",         p.getId());
        m.put("nickname",   p.getNickname());
        m.put("cash",       p.getCash());
        m.put("alive",      p.isAlive());
        m.put("position",   (p.getCurrentTile() != null ? p.getCurrentTile().getIndex() : -1));
        m.put("suspended",  p.isSuspended());
        m.put("suspendedRounds", p.getSuspensionRounds());
        m.put("escapeCard", p.hasEscapeCard());
        return m;
    }

    private static Map<String, Object> mapTile(Tile tile) {
        Map<String, Object> t = new HashMap<>();
        t.put("label", tile.getLabel());
        t.put("type",  tile.getClass().getSimpleName());
        t.put("index", tile.getIndex());

        if (tile instanceof StreetTile s) {
            t.put("ownerId",     s.getOwnerId());
            t.put("price",       s.getPrice());
            t.put("baseRent",    s.getBaseRent());
            t.put("houseCount",  s.getHouseCount());
            t.put("hotelCount",  s.getHotelCount());
            t.put("level",       s.getLevel().name());
        }

        return t;
    }
}
