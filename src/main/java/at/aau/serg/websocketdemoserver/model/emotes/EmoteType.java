package at.aau.serg.websocketdemoserver.model.emotes;

public enum EmoteType {
    MONEY("💰 Money time! 💰"),
    SHOCK("😱 Oh nein! 😱"),
    SORRY("🥺 Sorry! 🥺"),
    LAUGH("😂 Hahaha 😂"),
    THANKS("🥰 Danke 🥰"),
    GNADE("😓 Bitte habt Gnade 😓");

    private final String message;

    EmoteType(String message) { this.message = message; }
    public String getMessage() { return message; }
} //TODO: write documentation
