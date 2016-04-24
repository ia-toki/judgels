package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;
import java.util.Map;

public final class OutputOnlyGradingConfig extends SingleSourceFileWithoutSubtasksBlackBoxGradingConfig {
    private final String customScorer;

    public OutputOnlyGradingConfig(List<TestGroup> testData, String customScorer) {
        super(0, 0, testData);

        this.customScorer = customScorer;
    }

    @Override
    public Map<String, String> getSourceFileFields() {
        return ImmutableMap.of("source", "Output files (.zip)");
    }

    public String getCustomScorer() {
        return customScorer;
    }
}
