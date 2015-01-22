package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;
import java.util.Map;

public interface BlackBoxGradingConfig {
    List<TestSet> getSampleTestData();

    List<TestSet> getTestData();

    List<Subtask> getSubtasks();
}
