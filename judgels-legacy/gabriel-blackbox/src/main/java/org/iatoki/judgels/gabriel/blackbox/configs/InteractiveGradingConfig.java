package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class InteractiveGradingConfig extends SingleSourceFileWithoutSubtasksBlackBoxGradingConfig {
    private final String communicator;

    public InteractiveGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, String communicator) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);

        this.communicator = communicator;
    }

    public String getCommunicator() {
        return communicator;
    }
}
