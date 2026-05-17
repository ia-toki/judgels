package judgels.grading.engines;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import judgels.grading.api.GradingConfig;

public interface SingleSourceFileGradingConfig extends GradingConfig {
    @JsonIgnore
    @Override
    default Map<String, String> getSourceFileFields() {
        return ImmutableMap.of("source", "Source code");
    }
}
