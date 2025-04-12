package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.LobbyUpdatePayload;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.Player;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyService {
    private final Lobby lobby = new Lobby();

    public GameMessage handleJoinLobby(Object payload) {
        try {
            JSONObject obj = new JSONObject(payload.toString());
            String playerName = obj.getString("playerName");

            Player player = lobby.addPlayer(playerName);
            System.out.println("Neuer Spieler: " + player.getId());

            List<String> playerNames = lobby.getPlayers()
                    .stream()
                    .map(Player::getId)
                    .collect(Collectors.toList());

            return new GameMessage(MessageType.LOBBY_UPDATE, new LobbyUpdatePayload(playerNames));

        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Beitreten: " + e.getMessage());
        }
    }

    public GameMessage handleStartGame() {
        System.out.println("Spiel startet!");
        return new GameMessage(MessageType.START_GAME, "");
    }
}
