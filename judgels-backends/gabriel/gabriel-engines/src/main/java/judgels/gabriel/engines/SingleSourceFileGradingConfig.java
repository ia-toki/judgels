package judgels.gabriel.engines;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import judgels.gabriel.api.GradingConfig;

public interface SingleSourceFileGradingConfig extends GradingConfig {
    @Override
    default Map<String, String> getSourceFileFields() {
        return ImmutableMap.of("source", "Source Code");
    }
}
