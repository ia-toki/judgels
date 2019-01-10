package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class InteractiveWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksBlackBoxGradingConfig {
    private final String communicator;

    public InteractiveWithSubtasksGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, List<Integer> subtaskPoints, String communicator) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData, subtaskPoints);

        this.communicator = communicator;
    }

    public String getCommunicator() {
        return communicator;
    }
}
