package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;
import java.util.Map;

public abstract class SingleSourceFileBlackBoxGradingConfig extends AbstractBlackBoxGradingConfig {

    protected SingleSourceFileBlackBoxGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);
    }

    @Override
    public Map<String, String> getSourceFileFields() {
        return ImmutableMap.of("source", "Source Code");
    }
}
