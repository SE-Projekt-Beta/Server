package at.aau.serg.websocketdemoserver.websocket.broker;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Aktiviert einfache Broker-Ziele wie /topic oder /queue (z. B. für private Nachrichten)
        config.enableSimpleBroker("/topic", "/queue");

        // Alle Client-Nachrichten müssen mit /app beginnen
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Der zentrale STOMP-Endpunkt für WebSocket-Clients
        registry.addEndpoint("/dkt-websocket")
                .setAllowedOrigins("*"); // oder .setAllowedOriginPatterns("*") bei neueren Spring-Versionen

        // Optional für SockJS-Fallback (ältere Browser):
        // .withSockJS();
    }
}
