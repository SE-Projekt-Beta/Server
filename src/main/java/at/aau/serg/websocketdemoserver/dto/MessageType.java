package at.aau.serg.websocketdemoserver.dto;

public enum MessageType {
    ROLL_DICE,
    BUY_PROPERTY,
    PLAYER_MOVED,
    CAN_BUY_PROPERTY,
    MUST_PAY_RENT,
    PROPERTY_BOUGHT,
    PAY_TAX,
    DRAW_EVENT_RISIKO_CARD,
    DRAW_EVENT_BANK_CARD,
    GO_TO_JAIL,
    SKIPPED,
    LOBBY_UPDATE,
    START_GAME,
    JOIN_LOBBY,
    ERROR
}
