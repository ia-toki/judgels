package org.iatoki.judgels.sandalphon.client.lesson;

public final class ClientLesson {

    private final long id;
    private final String clientJid;
    private final String clientName;
    private final String lessonJid;
    private final String secret;

    public ClientLesson(long id, String clientJid, String clientName, String lessonJid, String secret) {
        this.id = id;
        this.clientJid = clientJid;
        this.clientName = clientName;
        this.lessonJid = lessonJid;
        this.secret = secret;
    }

    public long getId() {
        return id;
    }

    public String getClientJid() {
        return clientJid;
    }

    public String getClientName() {
        return clientName;
    }

    public String getLessonJid() {
        return lessonJid;
    }

    public String getSecret() {
        return secret;
    }
}
