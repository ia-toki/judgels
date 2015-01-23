package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.gabriel.GradingResult;

public final class BlackBoxGradingResponse implements GradingResponse {
    private final String submissionJid;
    private final BlackBoxGradingResult result;

    public BlackBoxGradingResponse(String submissionJid, BlackBoxGradingResult result) {
        this.submissionJid = submissionJid;
        this.result = result;
    }

    @Override
    public String getSubmissionJid() {
        return submissionJid;
    }

    @Override
    public GradingResult getResult() {
        return result;
    }
}
