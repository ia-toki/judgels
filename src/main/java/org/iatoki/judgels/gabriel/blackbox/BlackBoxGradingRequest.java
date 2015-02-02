package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;

public final class BlackBoxGradingRequest implements GradingRequest {
    private final String submissionJid;
    private final String problemJid;
    private final long problemLastUpdate;
    private final String gradingType;
    private final BlackBoxGradingSource gradingSource;

    public BlackBoxGradingRequest(String submissionJid, String problemJid, long problemLastUpdate, String gradingType, BlackBoxGradingSource gradingSource) {
        this.submissionJid = submissionJid;
        this.problemJid = problemJid;
        this.problemLastUpdate = problemLastUpdate;
        this.gradingType = gradingType;
        this.gradingSource = gradingSource;
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
    public String getGradingType() {
        return gradingType;
    }

    @Override
    public GradingSource getGradingSource() {
        return gradingSource;
    }
}
