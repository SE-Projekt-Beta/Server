package at.aau.serg.websocketdemoserver.model.gamestate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameStateConfig {

    @Bean
    public GameState gameState() {
        return new GameState();
    }
}
