package org.iatoki.judgels.gabriel.blackbox.configs;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MultipleSourceFilesBlackBoxGradingConfig extends AbstractBlackBoxGradingConfig {
    private final List<String> sourceFileFieldKeys;

    public MultipleSourceFilesBlackBoxGradingConfig(int timeLimitInMilliseconds, int memoryLimitInKilobytes, List<TestGroup> testData, List<String> sourceFileFieldKeys) {
        super(timeLimitInMilliseconds, memoryLimitInKilobytes, testData);
        this.sourceFileFieldKeys = sourceFileFieldKeys;
    }

    public List<String> getSourceFileFieldKeys() {
        return sourceFileFieldKeys;
    }

    @Override
    public Map<String, String> getSourceFileFields() {
        if (sourceFileFieldKeys.size() == 1) {
            return ImmutableMap.of(sourceFileFieldKeys.get(0), "Source Code");
        }

        Map<String, String> sourceFileFields = new LinkedHashMap<>();
        for (String key : sourceFileFieldKeys) {
            sourceFileFields.put(key, StringUtils.capitalize(key));
        }

        return ImmutableMap.copyOf(sourceFileFields);
    }
}
