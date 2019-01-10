package org.iatoki.judgels.gabriel;

public class FakeClientMessage {
    private final String type;
    private final String message;

    public FakeClientMessage(String targetChannel, String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getMessageType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
