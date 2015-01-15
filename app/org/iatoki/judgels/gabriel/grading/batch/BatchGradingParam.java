package org.iatoki.judgels.gabriel.grading.batch;

import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestSet;

import java.util.List;
import java.util.Set;

public final class BatchGradingParam {
    private final int timeLimitInMilliseconds;
    private final int memoryLimitInKilobytes;

    private final List<TestSet> testData;
    private final Set<Subtask> subtasks;

    public BatchGradingParam(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestSet> testData, Set<Subtask> subtasks) {
        this.timeLimitInMilliseconds = timeLimitInMilliseconds;
        this.memoryLimitInKilobytes = memoryLimitInKilobytes;
        this.testData = testData;
        this.subtasks = subtasks;
    }

    public int getTimeLimitInMilliseconds() {
        return timeLimitInMilliseconds;
    }

    public int getMemoryLimitInKilobytes() {
        return memoryLimitInKilobytes;
    }

    public List<TestSet> getTestData() {
        return testData;
    }

    public Set<Subtask> getSubtasks() {
        return subtasks;
    }
}
