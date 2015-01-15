package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;
import java.util.Set;

public interface GradingConfig {
    int getTimeLimitInMilliseconds();

    int getMemoryLimitInKilobytes();

    List<TestSet> getTestData();

    List<Subtask> getSubtasks();
}
