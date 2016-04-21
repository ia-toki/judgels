package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;
import java.util.Map;

public final class OutputOnlyGradingConfig extends SingleSourceFileWithoutSubtasksBlackBoxGradingConfig {

    public OutputOnlyGradingConfig(List<TestGroup> testData) {
        super(0, 0, testData);
    }

    @Override
    public Map<String, String> getSourceFileFields() {
        return ImmutableMap.of("source", "Output files (.zip)");
    }
}
