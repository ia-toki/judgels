package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class OutputOnlyWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksBlackBoxGradingConfig {

    public OutputOnlyWithSubtasksGradingConfig(List<TestGroup> testData, List<Integer> subtaskPoints) {
        super(0, 0, testData, subtaskPoints);
    }
}
