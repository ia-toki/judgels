package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public abstract class SingleSourceFileWithSubtasksBlackBoxGradingConfig extends SingleSourceFileBlackBoxGradingConfig {

    private final List<Integer> subtaskPoints;

    protected SingleSourceFileWithSubtasksBlackBoxGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, List<Integer> subtaskPoints) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);

        this.subtaskPoints = subtaskPoints;
    }

    @Override
    public List<Subtask> getSubtasks() {
        ImmutableList.Builder<Subtask> subtasks = ImmutableList.builder();
        for (int i = 0; i < subtaskPoints.size(); i++) {
            subtasks.add(new Subtask(i + 1, subtaskPoints.get(i), ""));
        }
        return subtasks.build();
    }
}
