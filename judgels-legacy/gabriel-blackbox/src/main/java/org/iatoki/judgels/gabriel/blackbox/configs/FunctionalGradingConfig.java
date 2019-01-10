package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class FunctionalGradingConfig extends MultipleSourceFilesBlackBoxGradingConfig {
    private final String customScorer;

    public FunctionalGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, List<String> sourceFileFieldKeys, String customScorer) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData, sourceFileFieldKeys);

        this.customScorer = customScorer;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return ImmutableList.of(new Subtask(-1, 100, ""));
    }

    public String getCustomScorer() {
        return customScorer;
    }
}
