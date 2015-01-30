package org.iatoki.judgels.gabriel.graders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.LanguageRestriction;
import org.iatoki.judgels.gabriel.blackbox.SampleTestCase;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;

public final class InteractiveWithSubtasksGradingConfig implements BlackBoxGradingConfig {
    public int timeLimitInMilliseconds;
    public int memoryLimitInKilobytes;
    public List<SampleTestCase> sampleTestData;
    public List<TestGroup> testData;
    public List<Integer> subtaskPoints;
    public String communicator;
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
    public List<SampleTestCase> getSampleTestData() {
        return sampleTestData;
    }

    @Override
    public List<TestGroup> getTestData() {
        return testData;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return Lists.transform(subtaskPoints, p -> new Subtask(p, ""));
    }

    public String getCommunicator() {
        return communicator;
    }

    public String getCustomScorer() {
        return customScorer;
    }

    @Override
    public List<String> getRequiredSourceFileKeys() {
        return ImmutableList.of("source");
    }

    @Override
    public LanguageRestriction getLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }
}
