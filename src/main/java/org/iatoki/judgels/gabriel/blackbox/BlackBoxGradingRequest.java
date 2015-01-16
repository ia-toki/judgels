package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingRequest;

import java.util.List;
import java.util.Map;

public final class BlackBoxGradingRequest implements GradingRequest {
    private final String senderChannel;
    private final String submissionJid;
    private final String problemJid;
    private final String gradingType;
    private final String language;
    private final Map<String, byte[]> sourceFiles;

    public BlackBoxGradingRequest(String senderChannel, String submissionJid, String problemJid, String gradingType, String language, Map<String, byte[]> sourceFiles) {
        this.senderChannel = senderChannel;
        this.submissionJid = submissionJid;
        this.problemJid = problemJid;
        this.gradingType = gradingType;
        this.language = language;
        this.sourceFiles = sourceFiles;
    }

    @Override
    public String getSenderChannel() {
        return senderChannel;
    }

    public String getSubmissionJid() {
        return submissionJid;
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

    public Map<String, byte[]> getSourceFiles() {
        return sourceFiles;
    }
}
