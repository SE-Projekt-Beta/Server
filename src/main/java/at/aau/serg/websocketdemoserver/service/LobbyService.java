package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LobbyService {

    private final Lobby lobby = new Lobby();
    private final GameHandler gameHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LobbyService(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public List<LobbyMessage> handle(LobbyMessage message) {
        if (message == null || message.getType() == null) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Ungültige oder fehlende Nachricht."));
        }

        return switch (message.getType()) {
            case JOIN_LOBBY -> handleJoinLobby(message.getPayload());
            case START_GAME -> handleStartGame();
            default -> List.of(new LobbyMessage(LobbyMessageType.ERROR, "Unbekannter Nachrichtentyp: " + message.getType()));
        };
    }

    private List<LobbyMessage> handleJoinLobby(Object payload) {
        try {
            JoinLobbyPayload joinPayload = objectMapper.convertValue(payload, JoinLobbyPayload.class);

            PlayerDTO playerDTO = lobby.addPlayer(joinPayload.getUsername());
            System.out.println("[LOBBY] Neuer Spieler: " + playerDTO.getNickname() + " (" + playerDTO.getId() + ")");

            return List.of(new LobbyMessage(
                    LobbyMessageType.LOBBY_UPDATE,
                    new LobbyUpdatePayload(lobby.getPlayers())
            ));
        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Fehler beim Beitreten: " + e.getMessage()));
        }
    }

    private List<LobbyMessage> handleStartGame() {
        if (!lobby.isReadyToStart()) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Mindestens 2 Spieler benötigt!"));
        }

        List<PlayerDTO> players = lobby.getPlayers();
        gameHandler.initGame(players); // Spiel initialisieren
        lobby.clear(); // Lobby nach Start leeren

        return List.of(new LobbyMessage(
                LobbyMessageType.START_GAME,
                new GameStartPayload(players)
        ));
    }
}
