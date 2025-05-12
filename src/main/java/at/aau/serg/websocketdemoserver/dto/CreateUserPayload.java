package at.aau.serg.websocketdemoserver.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreateUserPayload {
    private String username;
}