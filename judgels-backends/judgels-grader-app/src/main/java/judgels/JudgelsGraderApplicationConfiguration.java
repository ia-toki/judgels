package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import judgels.gabriel.GabrielConfiguration;

public class JudgelsGraderApplicationConfiguration extends Configuration {
    private final JudgelsGraderConfiguration judgelsConfig;
    private final GabrielConfiguration gabrielConfig;

    public JudgelsGraderApplicationConfiguration(
            @JsonProperty("judgels") JudgelsGraderConfiguration judgelsConfig,
            @JsonProperty("gabriel") GabrielConfiguration gabrielConfig) {

        this.judgelsConfig = judgelsConfig;
        this.gabrielConfig = gabrielConfig;
    }

    public JudgelsGraderConfiguration getJudgelsConfig() {
        return judgelsConfig;
    }

    public GabrielConfiguration getGabrielConfig() {
        return gabrielConfig;
    }
}
