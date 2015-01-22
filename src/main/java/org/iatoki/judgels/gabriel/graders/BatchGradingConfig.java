package org.iatoki.judgels.gabriel.graders;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestSet;

import java.util.List;

public final class BatchGradingConfig implements BlackBoxGradingConfig {
    private final int timeLimitInMilliseconds;
    private final int memoryLimitInKilobytes;

    private final List<TestSet> testData;
    private final List<Subtask> subtasks;

    private final String scoringExecutorFilename;

    public BatchGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestSet> testData, List<Subtask> subtasks, String scoringExecutorFilename) {
        this.timeLimitInMilliseconds = timeLimitInMilliseconds;
        this.memoryLimitInKilobytes = memoryLimitInKilobytes;
        this.testData = testData;
        this.subtasks = subtasks;
        this.scoringExecutorFilename = scoringExecutorFilename;
    }

    public static BatchGradingConfig createDefault() {
        ImmutableList.Builder<Subtask> subtasks = ImmutableList.builder();
        for (int i = 0; i < 10; i++) {
            subtasks.add(new Subtask(0, ""));
        }

        return new BatchGradingConfig(1000, 32000, ImmutableList.of(), subtasks.build(), null);
    }

    public int getTimeLimitInMilliseconds() {
        return timeLimitInMilliseconds;
    }

    public int getMemoryLimitInKilobytes() {
        return memoryLimitInKilobytes;
    }

    @Override
    public List<TestSet> getSampleTestData() {
        return ImmutableList.of();
    }

    @Override
    public List<TestSet> getTestData() {
        return testData;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return subtasks;
    }
}
