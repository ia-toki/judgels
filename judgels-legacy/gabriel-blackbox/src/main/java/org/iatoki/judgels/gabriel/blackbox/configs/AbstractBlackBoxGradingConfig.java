package org.iatoki.judgels.gabriel.blackbox.configs;

import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public abstract class AbstractBlackBoxGradingConfig implements BlackBoxGradingConfig {
    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;

    protected AbstractBlackBoxGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData) {
        this.timeLimit = timeLimitInMilliseconds;
        this.memoryLimit = memoryLimitInKilobytes;
        this.testData = testData;
    }

    @Override
    public final int getTimeLimitInMilliseconds() {
        return timeLimit;
    }

    @Override
    public final int getMemoryLimitInKilobytes() {
        return memoryLimit;
    }

    @Override
    public final List<TestGroup> getTestData() {
        return testData;
    }
}
