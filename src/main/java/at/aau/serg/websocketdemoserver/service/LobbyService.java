package at.aau.serg.websocketdemoserver.service;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import at.aau.serg.websocketdemoserver.dto.LobbyMessageType;
import at.aau.serg.websocketdemoserver.dto.GameStartPayload;


@Service
public class LobbyService {

    private final Lobby lobby = new Lobby();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GameHandler gameHandler;

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

            // Spieler anlegen
            Player player = lobby.addPlayer(joinPayload.getUsername());
            System.out.println("Neuer Spieler: " + player.getNickname() + " (" + player.getId() + ")");

            List<PlayerDTO> playerDTOs = toPlayerDTOs(lobby.getPlayers());

            return List.of(new LobbyMessage(
                    LobbyMessageType.LOBBY_UPDATE,
                    new LobbyUpdatePayload(playerDTOs)
            ));

        } catch (Exception e) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Fehler beim Beitreten: " + e.getMessage()));
        }
    }

    private List<LobbyMessage> handleStartGame() {
        if (!lobby.isReadyToStart()) {
            return List.of(new LobbyMessage(LobbyMessageType.ERROR, "Mindestens 2 Spieler nötig!"));
        }

        List<Player> players = lobby.getPlayers();
        List<PlayerDTO> playerDTOs = toPlayerDTOs(players);

        gameHandler.initGame(playerDTOs);

        return List.of(new LobbyMessage(
                LobbyMessageType.START_GAME,
                new GameStartPayload(playerDTOs)
        ));
    }

    private List<PlayerDTO> toPlayerDTOs(List<Player> players) {
        return players.stream()
                .map(p -> new PlayerDTO(p.getId(), p.getNickname()))
                .collect(Collectors.toList());
    }
}