package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class BinaryGradingConfig extends SingleSourceFileBlackBoxGradingConfig {
    private final String customScorer;

    public BinaryGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, String customScorer) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);

        this.customScorer = customScorer;
    }

    public String getCustomScorer() {
        return customScorer;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return ImmutableList.of(new Subtask(-1, 100, ""));
    }
}
