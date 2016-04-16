package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class OutputOnlyGradingConfig extends SingleSourceFileWithoutSubtasksBlackBoxGradingConfig {

    public OutputOnlyGradingConfig(List<TestGroup> testData) {
        super(0, 0, testData);
    }
}
