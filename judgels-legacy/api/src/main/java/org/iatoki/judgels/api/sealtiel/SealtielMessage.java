package org.iatoki.judgels.api.sealtiel;

public final class SealtielMessage {

    private final long id;
    private final String sourceClientJid;
    private final String sourceIPAddress;
    private final String messageType;
    private final String message;
    private final String timestamp;

    public SealtielMessage(long id, String sourceClientJid, String sourceIPAddress, String messageType, String message, String timestamp) {
        this.id = id;
        this.sourceClientJid = sourceClientJid;
        this.sourceIPAddress = sourceIPAddress;
        this.messageType = messageType;
        this.message = message;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getSourceClientJid() {
        return sourceClientJid;
    }

    public String getSourceIPAddress() {
        return sourceIPAddress;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
