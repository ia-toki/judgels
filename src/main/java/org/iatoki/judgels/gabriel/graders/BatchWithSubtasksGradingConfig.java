package org.iatoki.judgels.gabriel.graders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.LanguageRestriction;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class BatchWithSubtasksGradingConfig implements BlackBoxGradingConfig {
    public int timeLimitInMilliseconds;
    public int memoryLimitInKilobytes;
    public List<TestGroup> testData;
    public List<Integer> subtaskPoints;
    public String customScorer;

    @Override
    public int getTimeLimitInMilliseconds() {
        return timeLimitInMilliseconds;
    }

    @Override
    public int getMemoryLimitInKilobytes() {
        return memoryLimitInKilobytes;
    }

    @Override
    public List<TestGroup> getTestData() {
        return testData;
    }

    @Override
    public List<Subtask> getSubtasks() {
        ImmutableList.Builder<Subtask> subtasks = ImmutableList.builder();
        for (int i = 0; i < subtaskPoints.size(); i++) {
            subtasks.add(new Subtask(i + 1, subtaskPoints.get(i), ""));
        }
        return subtasks.build();
    }

    public String getCustomScorer() {
        return customScorer;
    }

    @Override
    public List<String> getRequiredSourceFileKeys() {
        return ImmutableList.of("Source");
    }

    @Override
    public LanguageRestriction getLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }
}
