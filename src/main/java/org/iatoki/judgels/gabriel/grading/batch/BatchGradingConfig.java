package org.iatoki.judgels.gabriel.grading.batch;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.GradingConfig;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestSet;

import java.util.List;

public final class BatchGradingConfig implements GradingConfig {
    private final int timeLimitInMilliseconds;
    private final int memoryLimitInKilobytes;

    private final List<TestSet> testData;
    private final List<Subtask> subtasks;

    public BatchGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestSet> testData, List<Subtask> subtasks) {
        this.timeLimitInMilliseconds = timeLimitInMilliseconds;
        this.memoryLimitInKilobytes = memoryLimitInKilobytes;
        this.testData = testData;
        this.subtasks = subtasks;
    }

    public BatchGradingConfig() {
        this(1000, 32000, ImmutableList.of(), createDefaultSubtasks());
    }

    @Override
    public int getTimeLimitInMilliseconds() {
        return timeLimitInMilliseconds;
    }

    @Override
    public int getMemoryLimitInKilobytes() {
        return memoryLimitInKilobytes;
    }

    @Override
    public List<TestSet> getTestData() {
        return testData;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    private static List<Subtask> createDefaultSubtasks() {
        ImmutableList.Builder<Subtask> subtasks = ImmutableList.builder();
        for (int i = 0; i < 10; i++) {
            subtasks.add(new Subtask(0.0, ""));
        }
        return subtasks.build();
    }
}
