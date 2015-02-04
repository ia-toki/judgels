package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class BatchWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksBlackBoxGradingConfig {
    private final String customScorer;

    public BatchWithSubtasksGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, List<Integer> subtaskPoints, String customScorer) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData, subtaskPoints);

        this.customScorer = customScorer;
    }

    public String getCustomScorer() {
        return customScorer;
    }
}
