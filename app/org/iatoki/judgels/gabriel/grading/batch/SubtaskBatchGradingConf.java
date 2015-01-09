package org.iatoki.judgels.gabriel.grading.batch;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.TestSet;

import java.util.List;

public final class SubtaskBatchGradingConf {

    public int timeLimit;
    public int memoryLimit;

    public List<TestSet> testSets;

    public SubtaskBatchGradingConf() {
        this(2, 1000, ImmutableList.of());
    }

    public SubtaskBatchGradingConf(int timeLimit, int memoryLimit, List<TestSet> testSets) {
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.testSets = ImmutableList.copyOf(testSets);
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public List<TestSet> getTestSets() {
        return testSets;
    }
}
