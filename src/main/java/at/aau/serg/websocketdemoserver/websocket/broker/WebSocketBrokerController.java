package at.aau.serg.websocketdemoserver.websocket.broker;

import at.aau.serg.websocketdemoserver.dkt.tiles.EventCard;
import at.aau.serg.websocketdemoserver.messaging.dtos.StompMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;


@Controller
public class WebSocketBrokerController {
    @MessageMapping("/hello")
    @SendTo("/topic/hello-response")
    public String handleHello(String text) {
        // TODO handle the messages here
        return "echo from broker: "+text;
    }
    @MessageMapping("/object")
    @SendTo("/topic/rcv-object")
    public StompMessage handleObject(StompMessage msg) {

       return msg;
    }

    @MessageMapping("/sendEventCards")
    @SendTo("/topic/event-cards")
    public EventCard sendEventCard(EventCard eventCard){

        return eventCard;
    }

}
