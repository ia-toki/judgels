package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;

public final class BlackBoxGradingRequest implements GradingRequest {
    private final String submissionJid;
    private final String problemJid;
    private final long gradingLastUpdateTime;
    private final String gradingType;
    private final BlackBoxGradingSource gradingSource;

    public BlackBoxGradingRequest(String submissionJid, String problemJid, long gradingLastUpdateTime, String gradingType, BlackBoxGradingSource gradingSource) {
        this.submissionJid = submissionJid;
        this.problemJid = problemJid;
        this.gradingLastUpdateTime = gradingLastUpdateTime;
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
    public long getGradingLastUpdateTime() {
        return gradingLastUpdateTime;
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
