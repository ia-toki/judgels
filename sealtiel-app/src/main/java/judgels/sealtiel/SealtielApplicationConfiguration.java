package judgels.sealtiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class SealtielApplicationConfiguration extends Configuration {
    private final SealtielConfiguration sealtielConfig;

    public SealtielApplicationConfiguration(
            @JsonProperty("sealtiel") SealtielConfiguration sealtielConfig) {

        this.sealtielConfig = sealtielConfig;
    }

    public SealtielConfiguration getSealtielConfig() {
        return sealtielConfig;
    }
}
