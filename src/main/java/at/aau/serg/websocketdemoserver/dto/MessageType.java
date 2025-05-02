package at.aau.serg.websocketdemoserver.dto;

public enum MessageType {
    ROLL_DICE,
    MOVE_PLAYER,
    START_MONEY,
    CAN_BUY_PROPERTY,
    BUY_PROPERTY,
    MUST_PAY_RENT,
    GO_TO_JAIL,
    PRISON_TASK,
    CASH_TASK,
    PROPERTY_LIST_UPDATE,
    PAY_TAX,
    SKIPPED,
    PLAYER_LOST,
    DRAW_EVENT_BANK_CARD,
    DRAW_EVENT_RISIKO_CARD,
    PLAYER_OUT_OF_JAIL_CARD,
    END_GAME,
    INIT_GAME,
    ERROR
}