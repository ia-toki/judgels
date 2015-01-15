package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingRequest;

public final class BlackBoxGradingRequest implements GradingRequest {
    private final long id;
    private final String senderChannel;
    private final String problemJid;
    private final String gradingType;
    private final String language;

    public BlackBoxGradingRequest(long id, String senderChannel, String problemJid, String gradingType, String language) {
        this.id = id;
        this.senderChannel = senderChannel;
        this.problemJid = problemJid;
        this.gradingType = gradingType;
        this.language = language;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getSenderChannel() {
        return senderChannel;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getGradingType() {
        return gradingType;
    }

    public String getLanguage() {
        return language;
    }
}
