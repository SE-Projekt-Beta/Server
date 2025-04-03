package at.aau.serg.websocketdemoserver.dkt;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class GameWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameHandler gameHandler = new GameHandler();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/dkt")
    @SendTo("/topic/dkt")
    public GameMessage handleGameMessage(@Payload GameMessage message) {
        System.out.println("DKT empfangen: " + message.getType());

        GameMessage result = gameHandler.handle(message);

        for (GameMessage extra : gameHandler.getExtraMessages()) {
            System.out.println("→ Extra: " + extra.getType());
            messagingTemplate.convertAndSend("/topic/dkt", extra);
        }

        return result;
    }

    @MessageMapping("/get_board")
    @SendTo("/topic/board")
    public String handleGetBoard(String input) throws Exception {
        // get the game board from the GameHandler
        GameBoard board = gameHandler.getGameBoard();
        return objectMapper.writeValueAsString(board);
    }

}
