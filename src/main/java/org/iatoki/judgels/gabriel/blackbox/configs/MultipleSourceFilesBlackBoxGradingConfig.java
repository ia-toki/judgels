package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MultipleSourceFilesBlackBoxGradingConfig extends AbstractBlackBoxGradingConfig {
    private final List<String> sourceFileFieldKeys;

    public MultipleSourceFilesBlackBoxGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, List<String> sourceFileFieldKeys) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);
        this.sourceFileFieldKeys = sourceFileFieldKeys;
    }

    @Override
    public Map<String, String> getSourceFileFields() {
        if (sourceFileFieldKeys.size() == 1) {
            return ImmutableMap.of(sourceFileFieldKeys.get(0), "Source Code");
        }

        return sourceFileFieldKeys.stream()
                .collect(Collectors.toMap(key -> key, key -> StringUtils.capitalize(key)));
    }
}
