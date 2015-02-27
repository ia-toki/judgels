package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.gabriel.GradingResult;

public final class BlackBoxGradingResponse implements GradingResponse {
    private final String gradingJid;
    private final BlackBoxGradingResult result;

    public BlackBoxGradingResponse(String gradingJid, BlackBoxGradingResult result) {
        this.gradingJid = gradingJid;
        this.result = result;
    }

    @Override
    public String getGradingJid() {
        return gradingJid;
    }

    @Override
    public GradingResult getResult() {
        return result;
    }
}
