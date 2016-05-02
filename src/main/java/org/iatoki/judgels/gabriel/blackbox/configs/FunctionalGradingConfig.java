package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class FunctionalGradingConfig extends MultipleSourceFilesBlackBoxGradingConfig {
    public FunctionalGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, List<String> sourceFileFieldKeys) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData, sourceFileFieldKeys);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return ImmutableList.of(new Subtask(-1, 100, ""));
    }
}
