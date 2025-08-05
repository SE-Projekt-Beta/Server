package at.aau.serg.websocketdemoserver.dto;

import at.aau.serg.websocketdemoserver.model.emotes.EmoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// repräsentiert eine Emote-Nachricht, die vom Server an alle Teilnehmer gesendet wird

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmoteMessagePayload {
    private String sender;
    private EmoteType emote;
}