package at.aau.serg.websocketdemoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebSocketDemoServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketDemoServerApplication.class, args);
    }

}
