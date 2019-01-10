package org.iatoki.judgels.api.sandalphon;

import java.util.Date;

public final class SandalphonProgrammingProblemInfo {

    private final String gradingEngine;
    private final long gradingLastUpdateTime;

    public SandalphonProgrammingProblemInfo(String gradingEngine, long gradingLastUpdateTime) {
        this.gradingEngine = gradingEngine;
        this.gradingLastUpdateTime = gradingLastUpdateTime;
    }

    public String getGradingEngine() {
        return gradingEngine;
    }

    public Date getGradingLastUpdateTime() {
        return new Date(gradingLastUpdateTime);
    }
}
