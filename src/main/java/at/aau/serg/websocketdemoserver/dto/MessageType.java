package at.aau.serg.websocketdemoserver.dto;

public enum MessageType {

    // Standardaktionen
    ROLL_DICE,
    MOVE_PLAYER,
    START_MONEY,
    SKIPPED,

    // Eigentum und Miete
    CAN_BUY_PROPERTY,
    BUY_PROPERTY,
    MUST_PAY_RENT,
    PROPERTY_LIST_UPDATE,

    // Sonderfelder & Gefängnis
    GO_TO_JAIL,
    PRISON_TASK,
    PLAYER_OUT_OF_JAIL_CARD,
    PLAYER_LOST,

    // Ereigniskarten (Bank & Risiko)
    DRAW_EVENT_BANK_CARD,
    DRAW_EVENT_RISIKO_CARD,

    // Tasks & Steuern
    CASH_TASK,
    PAY_TAX,

    // Spielende
    END_GAME,

    // Fehler
    ERROR
}
