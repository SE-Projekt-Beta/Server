package at.aau.serg.websocketdemoserver.dto;

import com.google.gson.Gson;

public class GameMessage {
    private MessageType type;
    private Object payload;

    private static final Gson gson = new Gson();

    public GameMessage() {}

    public GameMessage(MessageType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public static GameMessage error(String message) {
        return new GameMessage(MessageType.ERROR, message);
    }

    public <T> T parsePayload(Class<T> clazz) {
        return gson.fromJson(gson.toJson(payload), clazz);
    }
}
