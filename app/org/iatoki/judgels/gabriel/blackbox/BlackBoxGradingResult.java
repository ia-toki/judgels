package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingResult;

public final class BlackBoxGradingResult implements GradingResult {
    private long id;
    private String problemJid;
    private GradingVerdict verdict;

    public BlackBoxGradingResult(long id, String problemJid, GradingVerdict verdict) {
        this.id = id;
        this.problemJid = problemJid;
        this.verdict = verdict;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public GradingVerdict getVerdict() {
        return verdict;
    }
}
