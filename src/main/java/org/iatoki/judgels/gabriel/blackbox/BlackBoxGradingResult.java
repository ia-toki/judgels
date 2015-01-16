package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingResult;

public final class BlackBoxGradingResult implements GradingResult {
    private String submissionJid;
    private GradingVerdict verdict;

    public BlackBoxGradingResult(String submissionJid, GradingVerdict verdict) {
        this.submissionJid = submissionJid;
        this.verdict = verdict;
    }

    public String getSubmissionJid() {
        return submissionJid;
    }

    public GradingVerdict getVerdict() {
        return verdict;
    }
}
