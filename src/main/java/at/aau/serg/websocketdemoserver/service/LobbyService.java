package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.GameMessage;
import at.aau.serg.websocketdemoserver.dto.MessageType;
import at.aau.serg.websocketdemoserver.model.Player;
import org.json.JSONObject;

public class LobbyService {
    private final Lobby lobby = new Lobby();

    public GameMessage handleJoinLobby(String payload) {
        try {
            JSONObject obj = new JSONObject(payload);
            String username = obj.getString("username");

            Player player = lobby.addPlayer(username);
            System.out.println("Neuer Spieler beigetreten: " + player.getId());

            return new GameMessage(MessageType.LOBBY_UPDATE, lobby.toJson().toString());
        } catch (Exception e) {
            return new GameMessage(MessageType.ERROR, "Fehler beim Lobby-Beitritt: " + e.getMessage());
        }
    }

    public GameMessage handleStartGame() {
        System.out.println("Spiel startet!");
        return new GameMessage(MessageType.START_GAME, "");
    }

    public boolean isLobbyFull() {
        return lobby.isFull();
    }
}
