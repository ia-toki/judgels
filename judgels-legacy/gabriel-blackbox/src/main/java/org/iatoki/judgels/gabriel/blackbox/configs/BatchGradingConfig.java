package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class BatchGradingConfig extends SingleSourceFileWithoutSubtasksBlackBoxGradingConfig {
    private final String customScorer;

    public BatchGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, String customScorer) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);

        this.customScorer = customScorer;
    }

    public String getCustomScorer() {
        return customScorer;
    }
}
