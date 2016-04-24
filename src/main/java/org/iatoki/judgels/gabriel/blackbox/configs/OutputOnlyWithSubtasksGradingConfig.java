package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class OutputOnlyWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksBlackBoxGradingConfig {
    private final String customScorer;

    public OutputOnlyWithSubtasksGradingConfig(List<TestGroup> testData, List<Integer> subtaskPoints, String customScorer) {
        super(0, 0, testData, subtaskPoints);

        this.customScorer = customScorer;
    }

    public String getCustomScorer() {
        return customScorer;
    }
}
