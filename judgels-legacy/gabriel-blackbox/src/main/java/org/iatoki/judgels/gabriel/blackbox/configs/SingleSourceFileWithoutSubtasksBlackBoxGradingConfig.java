package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public abstract class SingleSourceFileWithoutSubtasksBlackBoxGradingConfig extends SingleSourceFileBlackBoxGradingConfig {
    protected SingleSourceFileWithoutSubtasksBlackBoxGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return ImmutableList.of(new Subtask(-1, 100, ""));
    }
}
