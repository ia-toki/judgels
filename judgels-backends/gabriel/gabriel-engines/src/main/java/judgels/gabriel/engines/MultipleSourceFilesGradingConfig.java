package judgels.gabriel.engines;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.gabriel.api.GradingConfig;
import org.apache.commons.lang3.StringUtils;

public interface MultipleSourceFilesGradingConfig extends GradingConfig {
    List<String> getSourceFileFieldKeys();

    @Override
    default Map<String, String> getSourceFileFields() {
        if (getSourceFileFieldKeys().size() == 1) {
            return ImmutableMap.of(getSourceFileFieldKeys().get(0), "Source Code");
        }

        Map<String, String> sourceFileFields = new LinkedHashMap<>();
        for (String key : getSourceFileFieldKeys()) {
            sourceFileFields.put(key, StringUtils.capitalize(key));
        }

        return ImmutableMap.copyOf(sourceFileFields);
    }
}
