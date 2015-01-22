package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingType;

import java.util.Map;

public final class BlackBoxGradingRequest implements GradingRequest {
    private final String submissionJid;
    private final String problemJid;
    private final long problemLastUpdate;
    private final GradingType gradingType;
    private final GradingLanguage gradingLanguage;
    private final Map<String, byte[]> sourceFiles;

    public BlackBoxGradingRequest(String submissionJid, String problemJid, long problemLastUpdate, GradingType gradingType, GradingLanguage gradingLanguage, Map<String, byte[]> sourceFiles) {
        this.submissionJid = submissionJid;
        this.problemJid = problemJid;
        this.problemLastUpdate = problemLastUpdate;
        this.gradingType = gradingType;
        this.gradingLanguage = gradingLanguage;
        this.sourceFiles = sourceFiles;
    }

    @Override
    public String getSubmissionJid() {
        return submissionJid;
    }

    @Override
    public String getProblemJid() {
        return problemJid;
    }

    @Override
    public long getProblemLastUpdate() {
        return problemLastUpdate;
    }

    @Override
    public GradingType getGradingType() {
        return gradingType;
    }

    @Override
    public GradingLanguage getGradingLanguage() {
        return gradingLanguage;
    }

    @Override
    public Map<String, byte[]> getSourceFiles() {
        return sourceFiles;
    }
}
