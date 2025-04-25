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
import at.aau.serg.websocketdemoserver.dto.GameStartPayload;


@Service
public class LobbyService {
    private final Lobby lobby = new Lobby();
    private final ObjectMapper objectMapper = new ObjectMapper(); // <---- NEU: sicheres JSON
    private final GameHandler gameHandler;

    public LobbyService(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }



    public List<LobbyMessage> handle(LobbyMessage message) {
        if (message == null || message.getType() == null) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Ungültige oder fehlende Nachricht."));
        }
        switch (message.getType()) {
            case JOIN_LOBBY:
                return handleJoinLobby(message.getPayload());
            case START_GAME:
                return handleStartGame(); // <-- hinzufügen!
            default:
                return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Unbekannter Typ: " + message.getType()));
        }
    }

    private List<LobbyMessage> handleJoinLobby(Object payload) {
        try {
            JoinLobbyPayload joinPayload = objectMapper.convertValue(payload, JoinLobbyPayload.class);

            // Spieler erstellen (oder ignorieren, falls bereits vorhanden)
            Player player = lobby.addPlayer(joinPayload.getUsername());

            System.out.println("Neuer Spieler: " + player.getUsername() + " (" + player.getId() + ")");

            // Spieler-DTOs erzeugen
            List<PlayerDTO> playerDTOs = lobby.getPlayers().stream()
                    .map(p -> new PlayerDTO(p.getId(), p.getUsername()))
                    .collect(Collectors.toList());

            LobbyUpdatePayload lobbyUpdatePayload = new LobbyUpdatePayload(playerDTOs);
            LobbyMessage updateMessage = new LobbyMessage(LobbyMessageType.LOBBY_UPDATE, lobbyUpdatePayload);

            return List.of(updateMessage);

        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Fehler beim Beitreten: " + e.getMessage()));
        }
    }

    private List<LobbyMessage> handleStartGame() {
        if (!lobby.isReadyToStart()) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Mindestens 2 Spieler nötig!"));
        }

        List<Player> players = lobby.getPlayers();

        List<PlayerDTO> order = players.stream()
                .map(p -> new PlayerDTO(p.getId(), p.getUsername()))
                .collect(Collectors.toList());

        gameHandler.initGame(players); // setzt Reihenfolge im Spielzustand

        return List.of(new LobbyMessage(LobbyMessageType.START_GAME, new GameStartPayload(order)));

    }


}