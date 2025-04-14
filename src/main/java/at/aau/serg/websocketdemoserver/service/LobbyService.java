package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;

@Service
public class LobbyService {
    private final Lobby lobby = new Lobby();
    private final ObjectMapper objectMapper = new ObjectMapper(); // <---- NEU: sicheres JSON

    public List<LobbyMessage> handle(LobbyMessage message) {
        switch (message.getType()) {
            case JOIN_LOBBY:
                return handleJoinLobby(message.getPayload());
            default:
                return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Unbekannter Typ: " + message.getType()));
        }
    }

    private List<LobbyMessage> handleJoinLobby(Object payload) {
        try {
            JoinLobbyPayload joinPayload = objectMapper.convertValue(payload, JoinLobbyPayload.class);
            Player player = lobby.addPlayer(joinPayload.getUsername());

            System.out.println("Neuer Spieler: " + player.getUsername() + " (" + player.getId() + ")");

            List<String> usernames = lobby.getPlayers()
                    .stream()
                    .map(Player::getUsername)
                    .collect(Collectors.toList());

            LobbyUpdatePayload lobbyUpdatePayload = new LobbyUpdatePayload(usernames);
            LobbyMessage updateMessage = new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, lobbyUpdatePayload);

            return List.of(updateMessage);

        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Fehler beim Beitreten: " + e.getMessage()));
        }
    }
}