package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingConfig;

import java.util.List;

public interface BlackBoxGradingConfig extends GradingConfig {

    List<String> getRequiredSourceFileKeys();

    LanguageRestriction getLanguageRestriction();

    int getTimeLimitInMilliseconds();

    int getMemoryLimitInKilobytes();

    List<TestGroup> getTestData();

    List<Subtask> getSubtasks();
}
