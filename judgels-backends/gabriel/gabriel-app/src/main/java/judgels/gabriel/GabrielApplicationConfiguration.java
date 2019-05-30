package judgels.gabriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class GabrielApplicationConfiguration extends Configuration {
    private final GabrielConfiguration gabrielConfig;

    public GabrielApplicationConfiguration(@JsonProperty("gabriel") GabrielConfiguration gabrielConfig) {
        this.gabrielConfig = gabrielConfig;
    }

    public GabrielConfiguration getGabrielConfig() {
        return gabrielConfig;
    }
}
