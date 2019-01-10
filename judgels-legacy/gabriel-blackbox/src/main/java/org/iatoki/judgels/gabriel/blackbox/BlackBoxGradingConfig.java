package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingConfig;

import java.util.List;
import java.util.Map;

public interface BlackBoxGradingConfig extends GradingConfig {

    Map<String, String> getSourceFileFields();

    int getTimeLimitInMilliseconds();

    int getMemoryLimitInKilobytes();

    List<TestGroup> getTestData();

    List<Subtask> getSubtasks();
}
