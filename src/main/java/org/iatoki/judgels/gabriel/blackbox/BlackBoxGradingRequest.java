package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;

public final class BlackBoxGradingRequest implements GradingRequest {
    private final String gradingJid;
    private final String problemJid;
    private final String gradingEngine;
    private final String gradingLanguage;
    private final BlackBoxGradingSource gradingSource;

    public BlackBoxGradingRequest(String gradingJid, String problemJid, String gradingEngine, String gradingLanguage, BlackBoxGradingSource gradingSource) {
        this.gradingJid = gradingJid;
        this.problemJid = problemJid;
        this.gradingEngine = gradingEngine;
        this.gradingLanguage = gradingLanguage;
        this.gradingSource = gradingSource;
    }

    @Override
    public String getGradingJid() {
        return gradingJid;
    }

    @Override
    public String getProblemJid() {
        return problemJid;
    }

    @Override
    public String getGradingEngine() {
        return gradingEngine;
    }

    @Override
    public String getGradingLanguage() {
        return gradingLanguage;
    }

    @Override
    public GradingSource getGradingSource() {
        return gradingSource;
    }
}
