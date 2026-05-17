package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

public class JudgelsGraderApplicationConfiguration extends Configuration {
    private final JudgelsGraderConfiguration judgelsConfig;

    public JudgelsGraderApplicationConfiguration(
            @JsonProperty("judgels") JudgelsGraderConfiguration judgelsConfig) {

        this.judgelsConfig = judgelsConfig;
    }

    public JudgelsGraderConfiguration getJudgelsConfig() {
        return judgelsConfig;
    }
}
