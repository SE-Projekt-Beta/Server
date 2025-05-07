package at.aau.serg.websocketdemoserver.service.lobby_request;

import at.aau.serg.websocketdemoserver.dto.*;
import at.aau.serg.websocketdemoserver.model.gamestate.GameBoard;
import at.aau.serg.websocketdemoserver.model.gamestate.GameState;
import at.aau.serg.websocketdemoserver.model.gamestate.Player;
import at.aau.serg.websocketdemoserver.service.GameManager;
import at.aau.serg.websocketdemoserver.service.Lobby;
import at.aau.serg.websocketdemoserver.service.LobbyManager;
import at.aau.serg.websocketdemoserver.service.LobbyRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class StartGameRequest implements LobbyRequest {

    private final LobbyManager lobbyManager;
    private final SimpMessagingTemplate messagingTemplate;

    public StartGameRequest(LobbyManager lobbyManager, SimpMessagingTemplate messagingTemplate) {
        this.lobbyManager = lobbyManager;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public List<LobbyMessage> handle(LobbyMessage message) {
        int lobbyId = message.getLobbyId();
        Lobby lobby = lobbyManager.getLobby(lobbyId);

        if (lobby == null || lobby.getPlayers().size() < 2) {
            return List.of(new LobbyMessage(
                    LobbyMessageType.ERROR,
                    "Cannot start game: Lobby missing or not enough players"
            ));
        }

        // Erstelle GameBoard
        GameBoard board = new GameBoard();

        // PlayerDTO → Player konvertieren
        List<Player> playerModels = lobby.getPlayers().stream()
                .map(dto -> new Player(dto.getNickname(), board))
                .collect(Collectors.toList());

        // Spiel starten: Spieler mischen und ersten Spieler festlegen
        GameState gameState = new GameState();
        gameState.startGame(playerModels);  // ✅ Reihenfolge zufällig, currentPlayer gesetzt
        GameManager.getInstance().registerGame(lobbyId, gameState);

        // PlayerDTOs (finale Spielerreihenfolge) für Clients erstellen
        List<PlayerDTO> finalDtos = gameState.getAllPlayers().stream()
                .map(p -> new PlayerDTO(p.getId(), p.getNickname()))
                .collect(Collectors.toList());

        // Sende START_GAME an alle Clients (Reihenfolge der Spieler)
        GameStartPayload payload = new GameStartPayload(finalDtos);
        LobbyMessage response = new LobbyMessage(lobbyId, LobbyMessageType.START_GAME, payload);
        messagingTemplate.convertAndSend("/topic/lobby", response);

        // Zusätzlich: CURRENT_PLAYER senden (wer ist zuerst dran?)
        sendCurrentPlayer(lobbyId, gameState.getCurrentPlayer().getId());

        return List.of(response);
    }

    private void sendCurrentPlayer(int lobbyId, int playerId) {
        GameMessage currentPlayerMessage = new GameMessage(
                lobbyId,
                MessageType.CURRENT_PLAYER,
                new CurrentPlayerPayload(playerId)
        );
        messagingTemplate.convertAndSend("/topic/dkt/" + lobbyId, currentPlayerMessage);
    }
}
