package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public interface BlackBoxGradingConfig {

    List<String> getRequiredSourceFileKeys();

    LanguageRestriction getLanguageRestriction();

    int getTimeLimitInMilliseconds();

    int getMemoryLimitInKilobytes();

    List<SampleTestCase> getSampleTestData();

    List<TestGroup> getTestData();

    List<Subtask> getSubtasks();
}
